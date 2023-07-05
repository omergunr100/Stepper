package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.step.definition.api.IStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class StepRunResult implements IStepRunResult {
    private final UUID runId;
    private final String name;
    private String alias;
    private final StepResult result;
    private final Instant startTime;
    private final Duration duration;
    private final String summary;
    private IStepExecutionContext context;
    private IStepDefinition stepDefinition;
    private String user;

    public StepRunResult(UUID runId, String name, StepResult result, Instant startTime, Duration duration, String summary) {
        this.runId = runId;
        this.name = name;
        this.result = result;
        this.startTime = startTime;
        this.duration = duration;
        this.summary = summary;
        this.context = null;
        this.stepDefinition = null;
    }

    @Override
    public UUID runId() {
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
    public Instant startTime() {
        return startTime;
    }

    @Override
    public Duration duration() {
        return duration;
    }

    @Override
    public String summary() {
        return summary;
    }

    @Override
    public void setContext(IStepExecutionContext context) {
        this.context = context;
    }

    @Override
    public IStepExecutionContext context() {
        return context;
    }

    @Override
    public void setStepDefinition(IStepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }

    @Override
    public IStepDefinition stepDefinition() {
        return stepDefinition;
    }

    @Override
    public String user() {
        return user;
    }
}
