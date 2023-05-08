package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.xml.generated.ex1.STFlow;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateNoDuplicateFlowOutputs implements IValidator {
    private STFlow flow;

    public ValidateNoDuplicateFlowOutputs(STFlow flow){
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if(flow.getSTFlowOutput().equals(""))
            return errors;

        Map<String, Integer> flowOutputs = new HashMap<>();

        for(String output : flow.getSTFlowOutput().split(","))
            flowOutputs.put(output, flowOutputs.getOrDefault(output, 0) + 1);

        flowOutputs
                .keySet()
                .stream()
                .filter(key -> flowOutputs.get(key) > 1)
                .forEach(key -> errors.add("Duplicate flow output name: " + key + " - shows up " + flowOutputs.get(key) + " times in flow: " + flow.getName()));

        return errors;
    }
}
