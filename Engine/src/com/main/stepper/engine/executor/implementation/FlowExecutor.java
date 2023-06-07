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
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class FlowExecutor implements IFlowExecutor {
    public static Property<IFlowRunResult> lastFlowResult = new SimpleObjectProperty<>();
    public static Property<IStepRunResult> lastStepResult = new SimpleObjectProperty<>();

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
            result.setAlias(step.name());
            stepRunUUID.add(result.runId());
            context.statistics().addRunResult(result);
            Platform.runLater(() -> lastStepResult.setValue(result));

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
        Duration duration = Duration.between(startTime, Instant.now());
        FlowRunResult flowRunResult = new FlowRunResult(context.getUniqueRunId(), flow.name(), flag, startTime, duration, userInputs, internalOutputs, formalOutputs, stepRunUUID);
        context.statistics().addRunResult(flowRunResult);
        Platform.runLater(() -> lastFlowResult.setValue(flowRunResult));
        return flowRunResult;
    }
}
