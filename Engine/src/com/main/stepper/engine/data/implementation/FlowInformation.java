package com.main.stepper.engine.data.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlowInformation implements IFlowInformation {
    private String name;
    private String description;
    private List<IDataIO> formalOutputs;
    private Boolean isReadOnly;
    private List<IStepUsageDeclaration> steps;
    private List<IDataIO> openUserInputs;
    private List<IDataIO> internalOutputs;
    private final Map<IDataIO, IStepUsageDeclaration> dataToProducer;
    private final Map<IDataIO, List<IStepUsageDeclaration>> dataToConsumer;

    public FlowInformation(String name, String description, List<IDataIO> formalOutputs, Boolean isReadOnly, List<IStepUsageDeclaration> steps, List<IDataIO> openUserInputs, List<IDataIO> internalOutputs, Map<IDataIO, IStepUsageDeclaration> dataToProducer, Map<IDataIO, List<IStepUsageDeclaration>> dataToConsumer) {
        this.name = name;
        this.description = description;
        this.formalOutputs = formalOutputs;
        this.isReadOnly = isReadOnly;
        this.steps = steps;
        this.openUserInputs = openUserInputs;
        this.internalOutputs = internalOutputs;
        this.dataToProducer = dataToProducer;
        this.dataToConsumer = dataToConsumer;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public List<IDataIO> formalOutputs() {
        return formalOutputs;
    }

    @Override
    public Boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public List<IStepUsageDeclaration> steps() {
        return steps;
    }

    @Override
    public List<IDataIO> openUserInputs() {
        return openUserInputs;
    }

    @Override
    public List<IDataIO> internalOutputs() {
        return internalOutputs;
    }

    @Override
    public List<IStepUsageDeclaration> linkedSteps(IDataIO dataIO) {
        return dataToConsumer.get(dataIO);
    }

    @Override
    public IStepUsageDeclaration producer(IDataIO dataIO) {
        return dataToProducer.get(dataIO);
    }
}
