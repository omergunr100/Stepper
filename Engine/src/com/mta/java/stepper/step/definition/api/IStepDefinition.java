package com.mta.java.stepper.step.definition.api;

import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;

import java.util.List;

public interface IStepDefinition {
    String getName();
    Boolean isReadOnly();
    List<IDataIO> getInputs();
    List<IDataIO> getOutputs();
    StepResult execute(IStepExecutionContext context);
}
