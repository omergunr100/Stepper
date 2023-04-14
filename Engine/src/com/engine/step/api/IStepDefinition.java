package com.engine.step.api;

import com.engine.io.api.IDataIO;

import java.util.List;

public interface IStepDefinition {
    String getName();
    Boolean isReadOnly();
    List<IDataIO> getInputs();
    List<IDataIO> getOutputs();
    StepResult execute(IStepExecutionContext context);
}
