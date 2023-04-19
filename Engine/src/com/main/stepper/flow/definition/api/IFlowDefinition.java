package com.main.stepper.flow.definition.api;

import com.main.stepper.io.api.IDataIO;

import java.util.List;
import java.util.Map;

public interface IFlowDefinition {
    String name();
    String description();
    Boolean isReadOnly();
    List<IStepUsageDeclaration> steps();
    void addStep(IStepUsageDeclaration step, Map<IDataIO, IDataIO> stepMapping);
    void addFormalOutput(IDataIO name);
    List<IDataIO> formalOutputNames();
    void addUserRequiredInput(IDataIO name);
    List<IDataIO> userRequiredInputs();
    Boolean validateFlowStructure();
}
