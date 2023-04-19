package com.main.stepper.flow.definition.implementation;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;

import java.util.*;

public class Flow implements IFlowDefinition {
    private final String name;
    private final String description;
    private Boolean readOnly;
    private final List<IDataIO> requiredInputs;
    private final List<IDataIO> formalOutputs;
    private final List<IStepUsageDeclaration> steps;
    private final Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mappings;

    public Flow(String name, String description) {
        this.name = name;
        this.description = description;
        requiredInputs = new ArrayList<>();
        formalOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        readOnly = null;
        this.mappings = new LinkedHashMap<>();
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
    public void addStep(IStepUsageDeclaration step, Map<IDataIO, IDataIO> stepMapping) {
        steps.add(step);
        mappings.put(step, stepMapping);
    }

    @Override
    public void addFormalOutput(IDataIO name) {
        formalOutputs.add(name);
    }

    @Override
    public void addUserRequiredInput(IDataIO name) {
        userRequiredInputs().add(name);
    }

    @Override
    public List<IDataIO> userRequiredInputs() {
        return userRequiredInputs();
    }

    @Override
    public List<IDataIO> formalOutputNames() {
        return formalOutputs;
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
