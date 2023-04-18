package com.main.stepper.flow.definition.implementation;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;

import java.util.ArrayList;
import java.util.List;

public class Flow implements IFlowDefinition {
    private final String name;
    private final String description;
    private Boolean readOnly;
    private final List<String> formalOutputs;
    private final List<IStepUsageDeclaration> steps;

    public Flow(String name, String description) {
        this.name = name;
        this.description = description;
        formalOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        readOnly = null;
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
    public Boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public List<IStepUsageDeclaration> steps() {
        return steps;
    }

    @Override
    public void addStep(IStepUsageDeclaration step) {
        steps.add(step);
    }

    @Override
    public void addFormalOutput(String name) {
        formalOutputs.add(name);
    }

    @Override
    public List<String> formalOutputNames() {
        return formalOutputs;
    }

    @Override
    public List<IDataIO> getInputs() {
        List<IDataIO> inputs = new ArrayList<>();
        for (int i = steps.size() - 1; i >= 0; i--) {
            IStepUsageDeclaration step = steps.get(i);
            inputs.addAll(step.step().getInputs());
            inputs.removeAll(step.step().getOutputs());
        }
        return inputs;
    }

    @Override
    public Boolean validateFlowStructure() {
        // Checks if the flow is readonly
        for(IStepUsageDeclaration step : steps)
            if(!step.step().isReadOnly())
                readOnly = false;
        if(readOnly == null)
            readOnly = true;

        //TODO: add logic to validate flow structure

        return true;
    }
}
