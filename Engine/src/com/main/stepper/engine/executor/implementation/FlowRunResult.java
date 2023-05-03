package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.io.api.IDataIO;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class FlowRunResult implements IFlowRunResult {
    private final String runId;
    private final String name;
    private final FlowResult result;
    private final Instant startTime;
    private final Duration duration;
    private final Map<IDataIO, Object> userInputs;
    private final Map<IDataIO, Object> internalOutputs;
    private final Map<IDataIO, Object> flowOutputs;
    private final List<String> stepRunUUID;

    public FlowRunResult(String runId, String name, FlowResult result, Instant startTime, Duration duration, Map<IDataIO, Object> userInputs, Map<IDataIO, Object> internalOutputs, Map<IDataIO, Object> flowOutputs, List<String> stepRunUUID) {
        this.runId = runId;
        this.name = name;
        this.result = result;
        this.startTime = startTime;
        this.duration = duration;
        this.userInputs = userInputs;
        this.internalOutputs = internalOutputs;
        this.flowOutputs = flowOutputs;
        this.stepRunUUID = stepRunUUID;
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
}
