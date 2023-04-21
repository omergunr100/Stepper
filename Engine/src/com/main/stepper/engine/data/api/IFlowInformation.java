package com.main.stepper.engine.data.api;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;

import java.util.List;

public interface IFlowInformation {
    String name();
    String description();
    List<IDataIO> formalOutputs();
    Boolean isReadOnly();
    List<IStepUsageDeclaration> steps();
    List<IDataIO> openUserInputs();
    List<IDataIO> internalOutputs();
    List<IStepUsageDeclaration> linkedSteps(IDataIO dataIO);
    IStepUsageDeclaration producer(IDataIO dataIO);
}
