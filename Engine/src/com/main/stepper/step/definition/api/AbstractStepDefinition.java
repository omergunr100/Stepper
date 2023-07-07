package com.main.stepper.step.definition.api;

import com.main.stepper.io.api.IDataIO;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractStepDefinition implements IStepDefinition{
    private final String name;
    private final Boolean readOnly;
    private final List<IDataIO> inputs;
    private final List<IDataIO> outputs;

    protected AbstractStepDefinition(String name, Boolean readOnly) {
        this.name = name;
        this.readOnly = readOnly;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    protected void addInput(IDataIO input) {
        inputs.add(input);
    }

    protected void addOutput(IDataIO output) {
        outputs.add(output);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public List<IDataIO> getInputs() {
        synchronized (inputs) {
            return new ArrayList<>(inputs);
        }
    }

    @Override
    public List<IDataIO> getOutputs() {
        synchronized (outputs) {
            return new ArrayList<>(outputs);
        }
    }

    @Override
    public StepDefinitionDTO toDTO() {
        return new StepDefinitionDTO(
                name,
                readOnly,
                inputs.stream().map(IDataIO::toDTO).collect(Collectors.toList()),
                outputs.stream().map(IDataIO::toDTO).collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", readOnly=" + readOnly +
                '}';
    }
}
