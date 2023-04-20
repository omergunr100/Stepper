package com.main.stepper.step.definition.api;

import com.main.stepper.io.api.IDataIO;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.util.List;

public interface IStepDefinition {
    String getName();
    Boolean isReadOnly();
    List<IDataIO> getInputs();
    List<IDataIO> getOutputs();
    StepResult execute(IStepExecutionContext context);
}