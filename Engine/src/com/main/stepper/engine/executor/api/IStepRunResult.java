package com.main.stepper.engine.executor.api;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.step.definition.api.IStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public interface IStepRunResult extends Serializable {
    String runId();

    String name();

    String alias();

    void setAlias(String alias);

    StepResult result();

    Instant startTime();

    Duration duration();

    String summary();

    void setContext(IStepExecutionContext context);

    IStepExecutionContext context();

    void setStepDefinition(IStepDefinition stepDefinition);

    IStepDefinition stepDefinition();
}
