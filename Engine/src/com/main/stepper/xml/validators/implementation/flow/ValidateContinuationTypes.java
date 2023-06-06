package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateContinuationTypes implements IValidator {
    private IFlowDefinition flow;

    public ValidateContinuationTypes(IFlowDefinition flow) {
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // Get all dataIOs from flow
        List<IDataIO> flowDataIOs = new ArrayList<>();
        flow.mappings().values().forEach(value -> flowDataIOs.addAll(value.values()));
        // for each continuation this flow has, check if data types match required and are inputs
        for(IFlowDefinition continuation : flow.continuations()) {
            // Get all dataIOs from continuation
            List<IDataIO> continuationDataIOs = new ArrayList<>();
            continuation.mappings().values().forEach(value -> continuationDataIOs.addAll(value.values()));

            // initialize continuation mappings map
            Map<IDataIO, IDataIO> continuationMappings = new HashMap<>();

            // check if custom mappings are of the same data type
            Map<String, String> customMappings = flow.customContinuationMapping(continuation.name());
            for(String src : customMappings.keySet()) {
                String dst = customMappings.get(src);
                // translate to IDataIOs
                List<IDataIO> srcMatches = flowDataIOs.stream().filter(dataIO -> dataIO.getName().equals(src)).collect(Collectors.toList());
                List<IDataIO> dstMatches = continuationDataIOs.stream().filter(dataIO -> dataIO.getName().equals(dst)).collect(Collectors.toList());
                // check if there is a source match
                if(srcMatches.isEmpty()) {
                    errors.add("Continuation custom mapping source " + src + " does not exist in flow " + flow.name());
                    return errors;
                }
                // check if there is a destination match
                if(dstMatches.isEmpty()) {
                    errors.add("Continuation custom mapping destination " + dst + " does not exist in flow " + continuation.name());
                    return errors;
                }
                // check if any of the destination matches are outputs
                if(dstMatches.stream().anyMatch(dataIO -> dataIO.getNecessity().equals(DataNecessity.NA))) {
                    errors.add("Continuation custom mapping destination " + dst + " is an output in flow " + continuation.name());
                    return errors;
                }
                // check if data types match
                if(!srcMatches.get(0).getDataDefinition().equals(dstMatches.get(0).getDataDefinition())) {
                    errors.add("Continuation custom mapping source " + src + " and destination " + dst + " do not have the same data type");
                    return errors;
                }
                // add to continuation mappings
                continuationMappings.put(dstMatches.get(0), srcMatches.get(0));
            }

            // add all other matching dataIOs to continuation mappings
            for(IDataIO flowDataIO : flowDataIOs) {
                List<IDataIO> continuationMatches = continuationDataIOs.stream().filter(dataIO -> dataIO.getName().equals(flowDataIO.getName())).collect(Collectors.toList());
                if(!continuationMatches.isEmpty())
                    if (continuationMatches.stream().noneMatch(dataIO -> dataIO.getNecessity().equals(DataNecessity.NA)))
                        continuationMappings.putIfAbsent(continuationMatches.get(0), flowDataIO);
            }

            // add all continuation mappings to flow
            flow.addContinuationMapping(continuation, continuationMappings);
        }

        return errors;
    }
}
