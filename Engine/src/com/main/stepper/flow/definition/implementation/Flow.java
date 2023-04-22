package com.main.stepper.flow.definition.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.data.implementation.FlowInformation;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateOutputNames;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoMultipleMandatoryInputsOfDifferentType;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoUnUserFriendlyMandatoryInputs;

import java.util.*;
import java.util.stream.Collectors;

public class Flow implements IFlowDefinition {
    private final String name;
    private final String description;
    private Boolean readOnly;
    private final List<IDataIO> requiredInputs; // Post-alias
    private final List<IDataIO> optionalInputs; // Post-alias
    private final List<IDataIO> formalOutputs; // Post-alias
    private final List<IDataIO> allOutputs; // Post-alias
    private final List<IStepUsageDeclaration> steps;
    private final Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mappings; // Step -> (Pre-alias -> Post-alias)
    private final Map<IDataIO, IStepUsageDeclaration> dataToMandatoryStep; // Post-alias -> Step, keeps track of which step needs the dataIO as mandatory input (first)
    private final Map<IDataIO, IStepUsageDeclaration> dataToProducer; // Post-alias -> Step, keeps track of which step produces which dataIO
    private final Map<IDataIO, List<IStepUsageDeclaration>> dataToConsumer; // Post-alias -> Step, keeps track of which steps consume which dataIO

    public Flow(String name, String description) {
        this.name = name;
        this.description = description;
        requiredInputs = new ArrayList<>();
        optionalInputs = new ArrayList<>();
        formalOutputs = new ArrayList<>();
        allOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        readOnly = null;
        this.mappings = new LinkedHashMap<>();
        this.dataToMandatoryStep = new HashMap<>();
        this.dataToProducer = new HashMap<>();
        this.dataToConsumer = new HashMap<>();
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
    public Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mappings() {
        return mappings;
    }

    @Override
    public void addStep(IStepUsageDeclaration step, Map<IDataIO, IDataIO> stepMapping) {
        // Add the step to the list of steps
        steps.add(step);
        mappings.put(step, stepMapping);
        allOutputs.addAll(step.step().getOutputs().stream().map(dataIO -> stepMapping.get(dataIO)).collect(Collectors.toList()));

        // Add the step as the producer of the dataIOs it produces
        stepMapping
                .values()
                .stream()
                .filter(data -> data.getNecessity().equals(DataNecessity.NA))
                .forEach(data -> dataToProducer.put(data, step));

        // Add the step as the consumer of the dataIOs it consumes
        stepMapping
                .values()
                .stream()
                .filter(data -> data.getNecessity().equals(DataNecessity.MANDATORY))
                .forEach(data -> {
                    if(!dataToConsumer.containsKey(data))
                        dataToConsumer.put(data, new ArrayList<>());
                    if(!dataToConsumer.get(data).contains(step))
                        dataToConsumer.get(data).add(step);
                    if(!dataToMandatoryStep.containsKey(data))
                        dataToMandatoryStep.put(data, step);
                });
        stepMapping
                .values()
                .stream()
                .filter(data -> data.getNecessity().equals(DataNecessity.OPTIONAL))
                .forEach(data -> {
                    if(!dataToConsumer.containsKey(data))
                        dataToConsumer.put(data, new ArrayList<>());
                    if(!dataToConsumer.get(data).contains(step))
                        dataToConsumer.get(data).add(step);
                });
    }

    @Override
    public void addFormalOutput(IDataIO name) {
        formalOutputs.add(name);
    }

    @Override
    public void addUserRequiredInput(IDataIO name) {
        requiredInputs.add(name);
    }

    @Override
    public List<IDataIO> userRequiredInputs() {
        return requiredInputs;
    }

    @Override
    public void addUserOptionalInput(IDataIO name) {
        optionalInputs.add(name);
    }

    @Override
    public List<IDataIO> userOptionalInputs() {
        return optionalInputs;
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

    @Override
    public IFlowInformation information() {
        List<IDataIO> openInputs = new ArrayList<>();
        openInputs.addAll(requiredInputs);
        openInputs.addAll(optionalInputs);

        IFlowInformation flowInformation = new FlowInformation(
                name,
                description,
                formalOutputs,
                readOnly,
                steps,
                openInputs,
                allOutputs,
                dataToProducer,
                dataToConsumer);
        return flowInformation;
    }

    @Override
    public String toString() {
        return "Flow{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public IStepUsageDeclaration stepRequiringMandatoryInput(IDataIO dataIO) {
        IStepUsageDeclaration step = dataToMandatoryStep.get(dataIO);
        if(step == null)
            step = dataToConsumer.get(dataIO).get(0);
        return step;
    }
}
