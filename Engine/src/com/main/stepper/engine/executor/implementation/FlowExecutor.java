package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IFlowExecutor;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.step.definition.api.IStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlowExecutor implements IFlowExecutor {

    public FlowExecutor() {
    }

    @Override
    public IFlowRunResult executeFlow(IFlowDefinition flow, IFlowExecutionContext context) {
        // Remember start time
        Instant startTime = Instant.now();
        // Default flag is success
        FlowResult flag = FlowResult.SUCCESS;
        Map<IDataIO, Object> userInputs = new LinkedHashMap<>();
        List<IDataIO> iterList;
        synchronized (flow) {
            iterList = new ArrayList<>(flow.userRequiredInputs());
        }
        for(IDataIO dataIO : iterList){
            Object value = context.getVariable(dataIO, dataIO.getDataDefinition().getType());
            if(value != null) {
                userInputs.put(dataIO, value);
            }
        }
        synchronized (flow) {
            iterList = new ArrayList<>(flow.userOptionalInputs());
        }
        for(IDataIO dataIO : iterList){
            Object value = context.getVariable(dataIO, dataIO.getDataDefinition().getType());
            if(value != null) {
                userInputs.put(dataIO, value);
            }
        }
        // generate a run history object and store it in the context
        IFlowRunResult thisFlowRunResult = new FlowRunResult(context.getUserCookie(), context.getUniqueRunId(), flow.name(), startTime, userInputs, context, flow);
        context.addFlowRunResult(thisFlowRunResult);

        List<String> stepRunUUID = new ArrayList<>();
        List<IStepUsageDeclaration> stepIterList;
        synchronized (flow) {
            stepIterList = new ArrayList<>(flow.steps());
        }
        for(IStepUsageDeclaration step : stepIterList){
            IStepExecutionContext stepContext = context.getStepExecutionContext(step);
            IStepDefinition stepDef = null;
            try {
                stepDef = step.step().getStepClass().newInstance();
            } catch (InstantiationException ignored) {
            } catch (IllegalAccessException ignored) {
            }

            IStepRunResult result = stepDef.execute(stepContext);
            result.setUser(context.getUserCookie());
            result.setContext(stepContext);
            result.setStepDefinition(step.step());
            result.setAlias(step.name());
            thisFlowRunResult.addStepRunUUID(result.runId());
            thisFlowRunResult.addStepRunResult(result);
            // store the step run result in the context
            context.addStepRunResult(result);

            if(result.result().equals(StepResult.WARNING)){
                flag = FlowResult.WARNING;
            }
            else if(result.result().equals(StepResult.FAILURE)){
                if(step.skipIfFailed()){
                    flag = FlowResult.WARNING;
                }
                else{
                    flag = FlowResult.FAILURE;
                    break;
                }
            }
        }

        // Add all internal flow outputs
        context.variables().keySet().stream().filter(dataIO -> !userInputs.keySet().contains(dataIO)).forEach(dataIO -> {
            thisFlowRunResult.addInternalOutput(dataIO,context.getVariable(dataIO, dataIO.getDataDefinition().getType()));
        });

        // Add all formal flow outputs
        synchronized (flow) {
            iterList = new ArrayList<>(flow.formalOutputs());
        }
        for (IDataIO dataIO : iterList) {
            if (dataIO != null) {
                thisFlowRunResult.addFlowOutput(dataIO, context.getVariable(dataIO, dataIO.getDataDefinition().getType()));
            }
        }
        thisFlowRunResult.setResult(flag);
        Duration duration = Duration.between(startTime, Instant.now());
        thisFlowRunResult.setDuration(duration);
        
        return thisFlowRunResult;
    }
}
