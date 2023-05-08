package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.xml.generated.ex1.STFlow;
import com.main.stepper.xml.generated.ex1.STStepInFlow;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.*;

public class ValidateNoDuplicateStepNamesInFlow implements IValidator {
    private STFlow flow;

    public ValidateNoDuplicateStepNamesInFlow(STFlow flow){
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        Map<String, Integer> duplicateCount = new HashMap<>();

        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow()){
            String finalName = Optional.ofNullable(step.getAlias()).orElse(step.getName());
            duplicateCount.put(finalName, duplicateCount.getOrDefault(finalName, 0) + 1);
        }
        duplicateCount
                .keySet()
                .stream()
                .filter(key -> duplicateCount.get(key) > 1)
                .forEach(key -> errors.add("Duplicate step name: " + key + " - shows up " + duplicateCount.get(key) + " times in flow: " + flow.getName()));

        return errors;
    }
}
