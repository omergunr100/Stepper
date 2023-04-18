package com.main.stepper.flow.definition.api;

import com.main.stepper.step.definition.api.IStepDefinition;

public interface IStepUsageDeclaration {
    String name();
    IStepDefinition step();
    Boolean skipIfFailed();
}
