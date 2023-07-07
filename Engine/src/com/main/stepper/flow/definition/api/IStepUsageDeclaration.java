package com.main.stepper.flow.definition.api;

import com.main.stepper.shared.structures.step.StepUsageDTO;
import com.main.stepper.step.definition.api.IStepDefinition;

import java.io.Serializable;

public interface IStepUsageDeclaration extends Serializable {
    String name();
    IStepDefinition step();
    Boolean skipIfFailed();
    StepUsageDTO toDTO();
}
