package com.main.stepper.flow.definition.api;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.io.api.IDataIO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IFlowDefinition extends Serializable {
    String name();
    String description();
    Boolean isReadOnly();
    List<IStepUsageDeclaration> steps();
    List<IFlowDefinition> continuations();
    void addContinuation(IFlowDefinition continuation);
    Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mappings();
    IDataIO mapsTo(IStepUsageDeclaration step, IDataIO io);
    void addStep(IStepUsageDeclaration step, Map<IDataIO, IDataIO> stepMapping);
    void addFormalOutput(IDataIO name);
    List<IDataIO> formalOutputs();
    void addUserRequiredInput(IDataIO name);
    List<IDataIO> userRequiredInputs();
    void addUserOptionalInput(IDataIO name);
    List<IDataIO> userOptionalInputs();
    List<String> validateFlowStructure();
    IFlowInformation information();
    IStepUsageDeclaration stepRequiringMandatoryInput(IDataIO dataIO);
}
