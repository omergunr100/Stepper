package com.mta.java.stepper.engine.executor.implementation;

import com.mta.java.stepper.engine.executor.api.IFlowExecutor;
import com.mta.java.stepper.engine.executor.api.IFlowRunResult;
import com.mta.java.stepper.flow.definition.api.FlowResult;
import com.mta.java.stepper.flow.definition.api.IFlowDefinition;
import com.mta.java.stepper.flow.definition.api.IStepUsageDeclaration;
import com.mta.java.stepper.flow.execution.api.IFlowExecutionContext;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.step.definition.api.StepResult;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;

public class FlowExecutor implements IFlowExecutor {
    private FlowExecutor() {
    }

    public static IFlowExecutor getInstance() {
        return new FlowExecutor();
    }

    @Override
    public IFlowRunResult executeFlow(IFlowDefinition flow, IFlowExecutionContext context) {
        Temporal startTime = LocalTime.now();

        FlowResult flag = FlowResult.SUCCESS;

        Map<IDataIO, Object> userInputs = new HashMap<>();
        for(IDataIO dataIO : flow.getInputs()){
            Object value = context.getVariable(dataIO, dataIO.getDataDefinition().getType());
            if(value != null) {
                userInputs.put(dataIO, value);
            }
        }
        // TODO: add flow run logic
        for(IStepUsageDeclaration step : flow.steps()){
            IStepExecutionContext stepContext = context.getStepExecutionContext(step);
            StepResult result = step.step().execute(stepContext);

            if(result == StepResult.FAILURE){
                if(step.skipIfFailed()){
                    flag = FlowResult.WARNING;
                }
                else{
                    flag = FlowResult.FAILURE;
                    break;
                }
            }
        }

        // TODO: check if output being formal means the step generating it is mandatory (no skip on fail)
        Map<IDataIO, Object> formalOutputs = new HashMap<>();
        if(flag != FlowResult.FAILURE) {
            for (String name : flow.formalOutputNames()) {
                IDataIO dataIO = context.getVariableIOByName(name);
                if (dataIO != null) {
                    formalOutputs.put(dataIO, context.getVariable(dataIO, dataIO.getDataDefinition().getType()));
                }
            }
        }
        Duration duration = Duration.between(startTime, LocalTime.now());
        // TODO: log flow run result for statistics in the engine
        return new FlowRunResult(context.getUniqueRunId(), flow.name(), flag, duration, userInputs, formalOutputs);
    }
}
