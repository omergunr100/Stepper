package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.definition.implementation.Flow;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateNoDuplicateOutputNames implements IValidator {
    private Flow flow;

    public ValidateNoDuplicateOutputNames(Flow flow){
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // Collect every step output in the flow and compare to previous outputs
        // if an output is already in the list then it's a duplicate
        Map<IDataIO, Integer> duplicateCount = new HashMap<>();

        for(IStepUsageDeclaration step : flow.steps()){
            List<IDataIO> stepOuts = step.step().getOutputs()
                    .stream()
                    .map(output -> flow.mapsTo(step, output))
                    .collect(Collectors.toList());
            // Add to count of duplicates
            stepOuts.stream()
                    //.filter(out -> duplicateCount.keySet().contains(out))
                    .forEach(out ->{
                        duplicateCount.put(out, duplicateCount.getOrDefault(out, 0) + 1);
                    });
        }

        // Create error list
        duplicateCount.keySet().stream()
                .filter(key -> duplicateCount.get(key) > 1)
                .forEach(key ->{
                    errors.add("Output: " + key.getName() + ", shows up: " + duplicateCount.get(key) + " times in flow.");
                });

        return errors;
    }
}
