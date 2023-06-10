package com.main.stepper.engine.executor.api;

import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.Serializable;
import java.time.Duration;

public interface IStepRunResult extends Serializable {
    String runId();

    String name();

    String alias();

    void setAlias(String alias);

    StepResult result();

    Duration duration();

    String summary();

    void setContext(IStepExecutionContext context);

    IStepExecutionContext context();
}
