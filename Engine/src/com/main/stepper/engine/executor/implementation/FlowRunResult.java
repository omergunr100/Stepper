package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.io.api.IDataIO;

import java.time.Duration;
import java.util.Map;

public class FlowRunResult implements IFlowRunResult {
    private final String runId;
    private final String name;
    private final FlowResult result;
    private final Duration duration;
    private final Map<IDataIO, Object> userInputs;
    private final Map<IDataIO, Object> flowOutputs;

    public FlowRunResult(String runId, String name, FlowResult result, Duration duration, Map<IDataIO, Object> userInputs, Map<IDataIO, Object> flowOutputs) {
        this.runId = runId;
        this.name = name;
        this.result = result;
        this.duration = duration;
        this.userInputs = userInputs;
        this.flowOutputs = flowOutputs;
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
    public Duration duration() {
        return duration;
    }

    @Override
    public Map<IDataIO, Object> userInputs() {
        return userInputs;
    }

    @Override
    public Map<IDataIO, Object> flowOutputs() {
        return flowOutputs;
    }
}
