package com.main.stepper.flow.definition.implementation;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateOutputNames;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoMultipleMandatoryInputsOfDifferentType;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoUnUserFriendlyMandatoryInputs;

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
    public IDataIO mapsTo(IStepUsageDeclaration step, IDataIO io) {
        Optional<Map<IDataIO, IDataIO>> stepMap = Optional.ofNullable(mappings.get(step));
        if(stepMap.isPresent())
            return stepMap.get().get(io);
        return null;
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
    public List<IDataIO> formalOutputs() {
        return formalOutputs;
    }

    @Override
    public List<String> validateFlowStructure() {
        List<String> errors = new ArrayList<>();
        // Checks if the flow is readonly
        for(IStepUsageDeclaration step : steps)
            if(!step.step().isReadOnly())
                readOnly = false;
        if(readOnly == null)
            readOnly = true;

        // validate no duplicate outputs (4.1)
        IValidator validateNoDuplicateOutputNames = new ValidateNoDuplicateOutputNames(this);
        errors.addAll(validateNoDuplicateOutputNames.validate());
        if(!errors.isEmpty())
            return errors;

        // validate no 'un-user friendly' mandatory inputs (4.2)
        IValidator validateNoUnUserFriendlyMandatoryInputs = new ValidateNoUnUserFriendlyMandatoryInputs(this);
        errors.addAll(validateNoUnUserFriendlyMandatoryInputs.validate());
        if(!errors.isEmpty())
            return errors;

        // TODO: validate no reference to undefined steps in flow (4.3) - note: maybe implement in MappingParser

        // TODO: validate no outputs come after inputs (4.3) - note: maybe implement in MappingParser

        // TODO: validate input/output type match (4.3) - note: maybe implement in MappingParser

        // TODO: validate no aliasing of steps/data that don't exist (4.4) - note: maybe implement in MappingParser

        // TODO: validate no flow formal output is undefined (4.5) - note: exception in FlowParser

        // validate multiple mandatory inputs with same name but different types (4.6)
        IValidator validateNoMultipleMandatoryInputsOfDifferentType = new ValidateNoMultipleMandatoryInputsOfDifferentType(this);
        errors.addAll(validateNoMultipleMandatoryInputsOfDifferentType.validate());
        if(!errors.isEmpty())
            return errors;

        return errors;
    }
}
