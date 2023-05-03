package com.main.stepper.engine.executor.api;

import com.main.stepper.step.definition.api.StepResult;

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
}
