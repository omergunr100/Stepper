package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.definition.implementation.Flow;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateInputOutputOrderAndType implements IValidator {
    private Flow flow;

    public ValidateInputOutputOrderAndType(Flow flow){
        this.flow = flow;
    }
    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        // Get all outputs and their generating step index
        Map<IDataIO, Integer> allOutputs = new HashMap<>();
        Map<IDataIO, IStepUsageDeclaration> outputToStep = new HashMap<>();
        for (int i = 0; i < flow.steps().size(); i++){
            IStepUsageDeclaration step = flow.steps().get(i);
            int finalI = i;
            flow.mappings().get(step).values().stream().filter(dataIO -> dataIO.getNecessity().equals(DataNecessity.NA)).forEach(output->{
                allOutputs.put(output, finalI);
                outputToStep.put(output, step);
            });
        }

        // Check all inputs for a match in the allOutputs map
        for(int i = 0; i < flow.steps().size(); i++){
            IStepUsageDeclaration step = flow.steps().get(i);
            int finalI = i;
            flow.mappings().get(step).values().stream().filter(dataIO -> !dataIO.getNecessity().equals(DataNecessity.NA)).forEach(input->{
                Optional<IDataIO> maybeOutput = allOutputs.keySet().stream().filter(dataIO -> dataIO.getName().equals(input.getName())).findFirst();
                if(maybeOutput.isPresent()){
                    // Check if the output is generated before the input is used
                    IDataIO output = maybeOutput.get();
                    if(allOutputs.get(output) > finalI){
                        errors.add("Flow: " + flow.name() + " - Input: " + input.getName() + " is generated after it is used.");
                    }
                    // Check if the input and output are of the same type
                    else if(!input.getDataDefinition().getType().equals(output.getDataDefinition().getType())){
                        errors.add("Flow: " + flow.name() + " - Input: " + input.getName() + " of step: " + step.name() + " and Output: " + output.getName() + " of step: " + outputToStep.get(output).name() + " are of different types.");
                    }
                }
            });
        }

        return errors;
    }
}
