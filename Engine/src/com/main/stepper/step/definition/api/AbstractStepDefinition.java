package com.main.stepper.step.definition.api;

import com.main.stepper.io.api.IDataIO;

import java.util.ArrayList;
import java.util.List;

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
        return inputs;
    }

    @Override
    public List<IDataIO> getOutputs() {
        return outputs;
    }
}
