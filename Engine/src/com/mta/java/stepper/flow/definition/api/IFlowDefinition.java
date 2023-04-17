package com.mta.java.stepper.flow.definition.api;

import com.mta.java.stepper.io.api.IDataIO;

import java.util.List;

public interface IFlowDefinition {
    String name();
    String description();
    Boolean isReadOnly();
    List<IStepUsageDeclaration> steps();
    void addStep(IStepUsageDeclaration step);
    void addFormalOutput(String name);
    List<String> formalOutputNames();
    List<IDataIO> getInputs();
    Boolean validateFlowStructure();
}
