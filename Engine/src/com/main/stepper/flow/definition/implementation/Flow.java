package com.main.stepper.flow.definition.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.data.implementation.FlowInformation;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.flow.ValidateInputOutputOrderAndType;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateOutputNames;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoMultipleMandatoryInputsOfDifferentType;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoUnUserFriendlyMandatoryInputs;

import java.util.*;
import java.util.stream.Collectors;

public class Flow implements IFlowDefinition {
    private final String name;
    private final String description;
    private Boolean readOnly;
    private List<String> continuationNames;
    private List<IFlowDefinition> continuations;
    private Map<String, Map<String, String>> customContinuationMappings;
    private Map<IFlowDefinition, Map<IDataIO, IDataIO>> continuationMappings;
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
        continuationNames = new ArrayList<>();
        continuationMappings = new HashMap<>();
        customContinuationMappings = new HashMap<>();
        continuations = new ArrayList<>();
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
    public void addCustomContinuationMapping(String continuation, Map<String, String> mapping) {
        customContinuationMappings.put(continuation, mapping);
    }

    @Override
    public Map<String, String> customContinuationMapping(String continuation) {
        return customContinuationMappings.get(continuation);
    }

    @Override
    public void addContinuationName(String continuationName) {
        continuationNames.add(continuationName);
    }

    @Override
    public List<String> continuationNames() {
        return continuationNames;
    }

    @Override
    public List<IFlowDefinition> continuations() {
        return continuations;
    }

    @Override
    public void addContinuationMapping(IFlowDefinition continuation, Map<IDataIO, IDataIO> mapping) {
        continuationMappings.put(continuation, mapping);
    }

    @Override
    public Map<IDataIO, IDataIO> continuationMapping(IFlowDefinition continuation) {
        return continuationMappings.getOrDefault(continuation, null);
    }

    @Override
    public void addContinuation(IFlowDefinition continuation) {
        continuations.add(continuation);
    }

    @Override
    public IDataIO mapsTo(IStepUsageDeclaration step, IDataIO io) {
        Optional<Map<IDataIO, IDataIO>> stepMap = Optional.ofNullable(mappings.get(step));
        return stepMap.map(iDataIOIDataIOMap -> iDataIOIDataIOMap.get(io)).orElse(null);
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
        allOutputs.addAll(step.step().getOutputs().stream().map(stepMapping::get).collect(Collectors.toList()));

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

        // validate no outputs come after inputs (4.3) and validate input/output type match (4.3)
        IValidator validateInputOutputOrderAndType = new ValidateInputOutputOrderAndType(this);
        errors.addAll(validateInputOutputOrderAndType.validate());
        if(!errors.isEmpty())
            return errors;

        // validate multiple mandatory inputs with same name but different types (4.6)
        IValidator validateNoMultipleMandatoryInputsOfDifferentType = new ValidateNoMultipleMandatoryInputsOfDifferentType(this);
        errors.addAll(validateNoMultipleMandatoryInputsOfDifferentType.validate());
        return errors;
    }

    @Override
    public IFlowInformation information() {
        List<IDataIO> openInputs = new ArrayList<>();
        openInputs.addAll(requiredInputs);
        openInputs.addAll(optionalInputs);

        return new FlowInformation(
                name,
                description,
                formalOutputs,
                readOnly,
                steps,
                openInputs,
                allOutputs,
                dataToProducer,
                dataToConsumer);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flow flow = (Flow) o;

        return Objects.equals(name, flow.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
