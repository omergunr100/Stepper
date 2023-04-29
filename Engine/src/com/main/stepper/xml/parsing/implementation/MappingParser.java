package com.main.stepper.xml.parsing.implementation;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.xml.generated.*;
import com.main.stepper.xml.parsing.api.IParser;

import java.util.*;
import java.util.stream.Collectors;

public class MappingParser implements IParser {
    private STFlow flow;
    private Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mapping;

    public MappingParser(STFlow flow) {
        this.flow = flow;
    }

    @Override
    public Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> parse() {
        List<String> errors = new ArrayList<>();

        // Get all steps in flow
        List<STStepInFlow> STSteps = flow.getSTStepsInFlow().getSTStepInFlow();

        // Parse steps in flow to usage declarations
        StepParser stepParser = new StepParser();

        // Apply flow level aliasing
        mapping = new LinkedHashMap<>();
        for(STStepInFlow STStep : STSteps) {
            errors.addAll(stepParser.load(STStep).parse());
            // Can't have a step with undefined name at this stage
            IStepUsageDeclaration step = stepParser.get();
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

        // Apply custom mapping
        // Get all custom mappings
        STCustomMappings stCustomMappings = flow.getSTCustomMappings();
        if(stCustomMappings != null){
            // Keep a tab on all the unprocessed custom mappings
            List<STCustomMapping> customMappings = new ArrayList<>();
            customMappings.addAll(stCustomMappings.getSTCustomMapping());

            List<STCustomMapping> used = new ArrayList<>();
            for(IStepUsageDeclaration step : mapping.keySet()){
                // Get the step mapping
                Map<IDataIO, IDataIO> stepMapping = mapping.get(step);
                List<IDataIO> aliasedIOs = new ArrayList<>(stepMapping.values());

                // Find custom mappings that reference steps that don't exist


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

        return mapping;
    }

    @Override
    public Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> get() {
        return mapping;
    }
}
