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
    void addCustomContinuationMapping(String continuation, Map<String, String> mapping);
    Map<String, String> customContinuationMapping(String continuation);
    void addContinuationName(String continuationName);
    List<String> continuationNames();
    void addContinuation(IFlowDefinition continuation);
    List<IFlowDefinition> continuations();
    void addContinuationMapping(IFlowDefinition continuation, Map<IDataIO, IDataIO> mapping);
    Map<IDataIO, IDataIO> continuationMapping(IFlowDefinition continuation);
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
    void addInitialValueRaw(String inputName, String value);
    Map<String, String> initialValuesRaw();
    void addInitialValue(IDataIO input, Object value);
    Map<IDataIO, Object> initialValues();
    IStepUsageDeclaration reverseMapDataIO(IDataIO dataIO);
}
