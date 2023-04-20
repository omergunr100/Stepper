package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.xml.generated.STFlow;
import com.main.stepper.xml.generated.STFlows;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ValidateNoDuplicateFlowNames implements IValidator {
    private STFlows flows;

    public ValidateNoDuplicateFlowNames(STFlows flows){
        this.flows = flows;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        Map<String, Integer> duplicateCount = new HashMap<>();

        for(STFlow flow : flows.getSTFlow())
            duplicateCount.put(flow.getName(), duplicateCount.getOrDefault(flow.getName(), 0) + 1);

        duplicateCount
                .keySet()
                .stream()
                .filter(key -> duplicateCount.get(key) > 1)
                .forEach(key -> errors.add("Duplicate flow name: " + key + " - shows up " + duplicateCount.get(key) + " times"));

        return errors;
    }
}
