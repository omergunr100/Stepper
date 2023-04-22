package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IFlowExecutor;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowExecutor implements IFlowExecutor {
    public FlowExecutor() {
    }

    @Override
    public List<IDataIO> userMandatoryInputs() {
        return null;
    }

    @Override
    public List<IDataIO> userOptionalInputs() {
        return null;
    }

    @Override
    public Boolean isReadyToExecute() {
        return null;
    }

    @Override
    public IFlowRunResult executeFlow(IFlowDefinition flow, IFlowExecutionContext context) {
        // Remember start time
        Temporal startTime = LocalTime.now();
        // Default flag is success
        FlowResult flag = FlowResult.SUCCESS;

        Map<IDataIO, Object> userInputs = new HashMap<>();
        for(IDataIO dataIO : flow.userRequiredInputs()){
            Object value = context.getVariable(dataIO, dataIO.getDataDefinition().getType());
            if(value != null) {
                userInputs.put(dataIO, value);
            }
        }

        for(IStepUsageDeclaration step : flow.steps()){
            IStepExecutionContext stepContext = context.getStepExecutionContext(step);
            IStepRunResult result = step.step().execute(stepContext);
            context.statistics().addRunResult(result);

            if(result.result().equals(StepResult.FAILURE)){
                if(step.skipIfFailed()){
                    // TODO: check if skip-if-failed step fails the flow should return warning
                    flag = FlowResult.WARNING;
                }
                else{
                    flag = FlowResult.FAILURE;
                    break;
                }
            }
        }

        // Add all internal flow outputs
        Map<IDataIO, Object> internalOutputs = new HashMap<>();
        context.variables().keySet().stream().filter(dataIO -> !userInputs.keySet().contains(dataIO)).forEach(dataIO -> {
            internalOutputs.put(dataIO, context.getVariable(dataIO, dataIO.getDataDefinition().getType()));
        });

        // Add all formal flow outputs
        Map<IDataIO, Object> formalOutputs = new HashMap<>();
        if(flag != FlowResult.FAILURE) {
            for (IDataIO dataIO : flow.formalOutputs()) {
                if (dataIO != null) {
                    formalOutputs.put(dataIO, context.getVariable(dataIO, dataIO.getDataDefinition().getType()));
                }
            }
        }
        Duration duration = Duration.between(startTime, LocalTime.now());
        FlowRunResult flowRunResult = new FlowRunResult(context.getUniqueRunId(), flow.name(), flag, startTime, duration, userInputs, internalOutputs, formalOutputs);
        context.statistics().addRunResult(flowRunResult);
        return flowRunResult;
    }
}
