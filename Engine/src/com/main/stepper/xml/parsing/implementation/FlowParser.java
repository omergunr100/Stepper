package com.main.stepper.xml.parsing.implementation;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.definition.implementation.Flow;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.generated.ex2.STContinuation;
import com.main.stepper.xml.generated.ex2.STFlow;
import com.main.stepper.xml.parsing.api.IParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlowParser implements IParser {
    private STFlow stflow;
    private Flow flow = null;

    public FlowParser(STFlow stflow){
        this.stflow = stflow;
    }

    public FlowParser load(STFlow stflow){
        this.stflow = stflow;
        this.flow = null;
        return this;
    }

    @Override
    public List<String> parse(){
        // Get flow properties and mapping
        flow = new Flow(stflow.getName(), stflow.getSTFlowDescription());
        // Get flow continuation names
        if(stflow.getSTContinuations() != null)
            for (STContinuation continuation : stflow.getSTContinuations().getSTContinuation())
                flow.addContinuationName(continuation.getTargetFlow());
        // Get flow mappings
        MappingParser mappingParser = new MappingParser(stflow);
        List<String> errors = mappingParser.parse();
        if(!errors.isEmpty())
            return errors;
        Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> flowMapping = mappingParser.get();
        // Add all step usages and mappings to flow
        flowMapping.keySet().stream().forEach(step -> flow.addStep(step, flowMapping.get(step)));
        // Get all outputs after aliasing
        List<IDataIO> outputs = new ArrayList<>();
        flowMapping.values().stream().forEach(map -> {
            outputs.addAll(map.values());
        });

        // Add formal outputs
        if(!stflow.getSTFlowOutput().equals("")){
            for(String formalName : stflow.getSTFlowOutput().split(",")) {
                Optional<IDataIO> match = outputs.stream().filter(dataIO -> dataIO.getName().equals(formalName)).findFirst();

                if(!match.isPresent())
                    errors.add("No match found for formal output name: " + formalName + " in flow: " + flow.name());
                else
                    flow.addFormalOutput(match.get());
            }

            if (!errors.isEmpty())
                return errors;
        }

        // Add user required inputs
        List<IDataIO> currOutputs = new ArrayList<>();
        for(IStepUsageDeclaration step : flow.steps()){
            // There is a match for each dataIO at this stage
            List<IDataIO> mappedInputs = step.step().getInputs().stream().map(value -> flowMapping.get(step).get(value)).collect(Collectors.toList());
            List<IDataIO> mappedOutputs = step.step().getOutputs().stream().map(value -> flowMapping.get(step).get(value)).collect(Collectors.toList());
            // Check if there is a matching output at this stage
            mappedInputs.removeAll(currOutputs);
            for(IDataIO input : mappedInputs){
                if(input.getNecessity() == DataNecessity.MANDATORY){
                    flow.addUserRequiredInput(input);
                    currOutputs.add(input);
                }
                else
                    flow.addUserOptionalInput(input);
            }
            currOutputs.addAll(mappedOutputs);
        }

        return errors;
    }

    @Override
    public Flow get() {
        return flow;
    }
}
