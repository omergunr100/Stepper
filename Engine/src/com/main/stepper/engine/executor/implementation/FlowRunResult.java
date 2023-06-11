package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class FlowRunResult implements IFlowRunResult {
    private final String runId;
    private final String name;
    private FlowResult result;
    private final Instant startTime;
    private Duration duration;
    private final Map<IDataIO, Object> userInputs;
    private final Map<IDataIO, Object> internalOutputs;
    private final Map<IDataIO, Object> flowOutputs;
    private final List<String> stepRunUUID;
    private final List<IStepRunResult> stepRunResults;
    private IFlowExecutionContext flowExecutionContext;
    private IFlowDefinition flowDefinition;

    public FlowRunResult(String runId, String name, FlowResult result, Instant startTime, Duration duration, Map<IDataIO, Object> userInputs, Map<IDataIO, Object> internalOutputs, Map<IDataIO, Object> flowOutputs, List<String> stepRunUUID, List<IStepRunResult> stepRunResults, IFlowExecutionContext context, IFlowDefinition flowDefinition) {
        this.runId = runId;
        this.name = name;
        this.result = result;
        this.startTime = startTime;
        this.duration = duration;
        this.userInputs = userInputs;
        this.internalOutputs = internalOutputs;
        this.flowOutputs = flowOutputs;
        this.stepRunUUID = stepRunUUID;
        this.stepRunResults = stepRunResults;
        this.flowExecutionContext = context;
        this.flowDefinition = flowDefinition;
    }

    public FlowRunResult(String runId, String name, Instant startTime, Map<IDataIO, Object> userInputs, IFlowExecutionContext context, IFlowDefinition flowDefinition) {
        this.runId = runId;
        this.name = name;
        this.result = FlowResult.RUNNING;
        this.startTime = startTime;
        this.userInputs = userInputs;
        this.flowExecutionContext = context;
        this.flowDefinition = flowDefinition;

        this.duration = null;
        this.internalOutputs = new HashMap<>();
        this.flowOutputs = new HashMap<>();
        this.stepRunUUID = new ArrayList<>();
        this.stepRunResults = new ArrayList<>();
    }

    @Override
    public String runId() {
        return runId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public FlowResult result() {
        return result;
    }

    @Override
    public Instant startTime() {
        return startTime;
    }

    @Override
    public Duration duration() {
        if (duration == null)
            return Duration.between(startTime, Instant.now());
        return duration;
    }

    @Override
    public Map<IDataIO, Object> userInputs() {
        return userInputs;
    }

    @Override
    public Map<IDataIO, Object> internalOutputs() {
        return internalOutputs;
    }

    @Override
    public Map<IDataIO, Object> flowOutputs() {
        return flowOutputs;
    }

    @Override
    public List<String> stepRunUUID() {
        return stepRunUUID;
    }

    @Override
    public List<IStepRunResult> stepRunResults() {
        return stepRunResults;
    }

    @Override
    public void setResult(FlowResult result) {
        this.result = result;
    }

    @Override
    public void addInternalOutput(IDataIO dataIO, Object value) {
        internalOutputs.put(dataIO, value);
    }

    @Override
    public void addFlowOutput(IDataIO dataIO, Object value) {
        flowOutputs.put(dataIO, value);
    }

    @Override
    public void addStepRunUUID(String stepRunUUID) {
        this.stepRunUUID.add(stepRunUUID);
    }

    @Override
    public void addStepRunResult(IStepRunResult stepRunResult) {
        stepRunResults.add(stepRunResult);
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public IFlowExecutionContext flowExecutionContext() {
        return flowExecutionContext;
    }

    @Override
    public IFlowDefinition flowDefinition() {
        return flowDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowRunResult that = (FlowRunResult) o;

        return Objects.equals(runId, that.runId);
    }

    @Override
    public int hashCode() {
        return runId != null ? runId.hashCode() : 0;
    }
}
