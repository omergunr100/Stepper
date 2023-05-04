package com.main.stepper.xml.parsing.implementation;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.xml.generated.*;
import com.main.stepper.xml.parsing.api.IParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingParser implements IParser {
    private STFlow flow;
    private Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mapping;

    public MappingParser(STFlow flow) {
        this.flow = flow;
    }

    @Override
    public List<String> parse() {
        List<String> errors = new ArrayList<>();

        // Get all steps in flow
        List<STStepInFlow> STSteps = flow.getSTStepsInFlow().getSTStepInFlow();
        List<IStepUsageDeclaration> stepsInFlow = new ArrayList<>();

        // Parse steps in flow to usage declarations
        StepParser stepParser = new StepParser();

        // Apply flow level aliasing (and validate non-existent steps)
        mapping = new LinkedHashMap<>();
        for(STStepInFlow STStep : STSteps) {
            List<String> stepParserErrors = stepParser.load(STStep).parse();
            stepParserErrors.stream().forEach(error -> errors.add("Flow: " + flow.getName() + " - " + error));
            if(!errors.isEmpty())
                return errors;
            // Can't have a step with undefined name at this stage
            IStepUsageDeclaration step = stepParser.get();
            stepsInFlow.add(step);
            // Get the mapping for the step
            Map<IDataIO, IDataIO> stepMapping = new HashMap<>();
            // Contains all DataIOs in the step
            List<IDataIO> stepDataIOs = new ArrayList<>();
            stepDataIOs.addAll(step.step().getInputs());
            stepDataIOs.addAll(step.step().getOutputs());

            stepMapping
                    .putAll(
                            stepDataIOs
                                    .stream()
                                    .collect(Collectors.toMap(dataIO -> dataIO, dataIO -> dataIO))
                    );

            // Apply flow level aliasing
            STFlowLevelAliasing flowLevelAliasing = flow.getSTFlowLevelAliasing();
            if(flowLevelAliasing != null){
                List<STFlowLevelAlias> flowLevelAlias = flow.getSTFlowLevelAliasing().getSTFlowLevelAlias()
                        .stream()
                        .filter(alias -> alias.getStep().equals(step.name()))
                        .collect(Collectors.toList());

                for(STFlowLevelAlias alias : flowLevelAlias){
                    stepDataIOs.stream()
                            .filter(dataIO -> dataIO.getName().equals(alias.getSourceDataName()))
                            .forEach(dataIO -> stepMapping.put(dataIO, new DataIO(alias.getAlias(), dataIO.getUserString(), dataIO.getNecessity(), dataIO.getDataDefinition())));
                }

            }
            mapping.put(step, stepMapping);
        }

        // Validate flow level aliasing
        if(flow.getSTFlowLevelAliasing() != null){
            for(STFlowLevelAlias alias : flow.getSTFlowLevelAliasing().getSTFlowLevelAlias()){
                // if there isn't a step by the referenced name
                if (mapping.keySet().stream().filter(step -> step.name().equals(alias.getStep())).count() != 1) {
                    errors.add("Flow: " + flow.getName() + " - " + "Flow level aliasing: " + alias.getStep() + " is not a valid step name.");
                }
                else{
                    // if there is no dataIO by the referenced name in the step
                    IStepUsageDeclaration stepUsage = mapping.keySet().stream().filter(step -> step.name().equals(alias.getStep())).findAny().get();
                    Stream<IDataIO> dataIOStream = mapping.get(stepUsage).keySet().stream().filter(dataIO -> dataIO.getName().equals(alias.getSourceDataName()));
                    if(dataIOStream.count() != 1)
                        errors.add("Flow: " + flow.getName() + " - " + "Flow level aliasing: " + alias.getSourceDataName() + " is not a valid data member name in step: " + stepUsage.name());
                }
            }

            if(!errors.isEmpty())
                return errors;
        }

        // Validate no


        // Get all custom mappings
        STCustomMappings stCustomMappings = flow.getSTCustomMappings();
        if(stCustomMappings != null){
            // Validate custom mappings
            for(STCustomMapping custom : stCustomMappings.getSTCustomMapping()){
                int sourceLoc = -1;
                int targetLoc = -1;
                for(int i = 0; i < stepsInFlow.size(); i++){
                    if(stepsInFlow.get(i).name().equals(custom.getSourceStep()))
                        sourceLoc = i;
                    if(stepsInFlow.get(i).name().equals(custom.getTargetStep()))
                        targetLoc = i;
                }

                // Check for non-existent source step
                if(sourceLoc == -1) {
                    errors.add("Flow: " + flow.getName() + " - " + "Custom mapping: " + custom.getSourceStep() + " is not a valid step name.");
                    return errors;
                }
                // Check for non-existent source dataIO / not output
                else{
                    IStepUsageDeclaration source = stepsInFlow.get(sourceLoc);
                    if(mapping.get(source).values().stream().filter(dataIO->dataIO.getNecessity().equals(DataNecessity.NA)).filter(dataIO->dataIO.getName().equals(custom.getSourceData())).count() != 1) {
                        errors.add("Flow: " + flow.getName() + " - " + "Custom mapping: " + custom.getSourceData() + " is not a valid output name in step: " + source.name());
                        return errors;
                    }
                }
                // Check for non-existent target step
                if (targetLoc == -1) {
                    errors.add("Flow: " + flow.getName() + " - " + "Custom mapping: " + custom.getTargetStep() + " is not a valid step name.");
                    return errors;
                }
                // Check for non-existent target dataIO / not input
                else{
                    IStepUsageDeclaration target = stepsInFlow.get(targetLoc);
                    if(mapping.get(target).values().stream().filter(dataIO->!dataIO.getNecessity().equals(DataNecessity.NA)).filter(dataIO->dataIO.getName().equals(custom.getTargetData())).count() != 1) {
                        errors.add("Flow: " + flow.getName() + " - " + "Custom mapping: " + custom.getTargetData() + " is not a valid input name in step: " + target.name());
                        return errors;
                    }
                }

                // Check if source comes before target
                if(sourceLoc > targetLoc) {
                    errors.add("Flow: " + flow.getName() + " - " + "Custom mapping: " + custom.getSourceStep() + " must come before " + custom.getTargetStep() + ".");
                    return errors;
                }
            }

            if(!errors.isEmpty())
                return errors;

            // Apply custom mapping
            List<STCustomMapping> customMappings = new ArrayList<>();
            customMappings.addAll(stCustomMappings.getSTCustomMapping());

            List<STCustomMapping> used = new ArrayList<>();
            for(IStepUsageDeclaration step : mapping.keySet()){
                // Get the step mapping
                Map<IDataIO, IDataIO> stepMapping = mapping.get(step);
                List<IDataIO> aliasedIOs = new ArrayList<>(stepMapping.values());

                // For each custom mapping for the current step:
                // find the correlated aliased dataIO and replace it with the custom mapping
                customMappings
                        .stream()
                        .filter(mapping -> mapping.getTargetStep().equals(step.name()))
                        .forEach(mapping -> {
                            aliasedIOs.stream()
                                    .filter(dataIO -> dataIO.getName().equals(mapping.getTargetData()))
                                    .forEach(dataIO -> {
                                                stepMapping.put(stepMapping.keySet()
                                                        .stream()
                                                        .filter(key -> stepMapping.get(key).equals(dataIO))
                                                        .findAny().get(),
                                                        new DataIO(mapping.getSourceData(), dataIO.getUserString(), dataIO.getNecessity(), dataIO.getDataDefinition()));
                                            });
                        });
            }
        }

        return errors;
    }

    @Override
    public Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> get() {
        return mapping;
    }
}
