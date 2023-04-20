package com.main.stepper.xml.parsing.implementation;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.definition.implementation.Flow;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.generated.STFlow;
import com.main.stepper.xml.parsing.api.IParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Flow parse() throws Exception {
        // Get flow properties and mapping
        flow = new Flow(stflow.getName(), stflow.getSTFlowDescription());
        Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> flowMapping = new MappingParser(stflow).parse();
        // Add all step usages and mappings to flow
        flowMapping.keySet().stream().forEach(step -> flow.addStep(step, flowMapping.get(step)));
        // Get all outputs after aliasing
        List<IDataIO> outputs = new ArrayList<>();
        flowMapping.values().stream().forEach(map -> {
            outputs.addAll(map.values());
        });

        // Add formal outputs
        for(String formalName : stflow.getSTFlowOutput().split(",")) {
            Optional<IDataIO> match = outputs.stream().filter(dataIO -> dataIO.getName().equals(formalName)).findFirst();

            if(!match.isPresent())
                throw new Exception("No match found for formal output name: " + formalName);

            flow.addFormalOutput(match.get());
        }

        // Add user required inputs
        List<IDataIO> currOutputs = new ArrayList<>();
        for(IStepUsageDeclaration step : flow.steps()){
            // There is a match for each dataIO at this stage
            IDataIO input = step.step().getInputs().stream().map(value -> flowMapping.get(step).get(value)).findFirst().get();
            // Check if there is a matching output at this stage
            if(!currOutputs.contains(input)){
                flow.addUserRequiredInput(input);
                currOutputs.add(input);
            }
        }

        return flow;
    }

    @Override
    public Flow get() {
        return flow;
    }
}
