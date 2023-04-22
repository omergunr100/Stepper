package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.step.definition.api.StepResult;

import java.time.Duration;

public class StepRunResult implements IStepRunResult {
    private final String runId;
    private final String name;
    private String alias;
    private final StepResult result;
    private final Duration duration;
    private final String summary;

    public StepRunResult(String runId, String name, StepResult result, Duration duration, String summary) {
        this.runId = runId;
        this.name = name;
        this.result = result;
        this.duration = duration;
        this.summary = summary;
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
    public String alias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public StepResult result() {
        return result;
    }

    @Override
    public Duration duration() {
        return duration;
    }

    @Override
    public String summary() {
        return summary;
    }
}
