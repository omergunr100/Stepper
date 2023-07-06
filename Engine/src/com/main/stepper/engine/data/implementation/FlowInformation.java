package com.main.stepper.engine.data.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.step.StepDTO;

import java.util.*;
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
    private final List<String> continuations;

    public FlowInformation(String name, String description, List<IDataIO> formalOutputs, Boolean isReadOnly, List<IStepUsageDeclaration> steps, List<IDataIO> openUserInputs, List<IDataIO> internalOutputs, Map<IDataIO, IStepUsageDeclaration> dataToProducer, Map<IDataIO, List<IStepUsageDeclaration>> dataToConsumer, List<String> continuations) {
        this.name = name;
        this.description = description;
        this.formalOutputs = formalOutputs;
        this.isReadOnly = isReadOnly;
        this.steps = steps;
        this.openUserInputs = openUserInputs;
        this.internalOutputs = internalOutputs;
        this.dataToProducer = dataToProducer;
        this.dataToConsumer = dataToConsumer;
        this.continuations = continuations;
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

    @Override
    public List<String> continuations() {
        return continuations;
    }

    @Override
    public FlowInfoDTO toDTO() {
        HashMap<DataIODTO, List<StepDTO>> newDataToConsumer = new HashMap<>();
        for (Map.Entry<IDataIO, List<IStepUsageDeclaration>> entry : dataToConsumer.entrySet())
            newDataToConsumer.put(entry.getKey().toDTO(), entry.getValue().stream().map(IStepUsageDeclaration::toDTO).collect(Collectors.toList()));
        HashMap<DataIODTO, StepDTO> newDataToProducer = new HashMap<>();
        for (Map.Entry<IDataIO, IStepUsageDeclaration> entry : dataToProducer.entrySet())
            newDataToProducer.put(entry.getKey().toDTO(), entry.getValue().toDTO());
        return new FlowInfoDTO(
                name,
                description,
                steps.stream().map(IStepUsageDeclaration::toDTO).collect(Collectors.toList()),
                openUserInputs.stream().map(IDataIO::toDTO).collect(Collectors.toList()),
                continuations,
                isReadOnly,
                formalOutputs.stream().map(IDataIO::toDTO).collect(Collectors.toList()),
                newDataToConsumer,
                newDataToProducer,
                internalOutputs.stream().map(IDataIO::toDTO).collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowInformation that = (FlowInformation) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
