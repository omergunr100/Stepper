package com.mta.java.stepper.flow.definition.api;

import com.mta.java.stepper.step.definition.api.IStepDefinition;

public interface IStepUsageDeclaration {
    String name();
    IStepDefinition step();
    Boolean skipIfFailed();
}
