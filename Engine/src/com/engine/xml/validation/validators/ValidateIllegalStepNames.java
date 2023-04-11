package com.engine.xml.validation.validators;

import com.engine.xml.flow.STFlow;
import com.engine.xml.steps.STStepInFlow;
import com.engine.xml.validation.STEPS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidateIllegalStepNames implements IValidator{
    private STFlow flow;
    private List<String> errors;
    private ValidateIllegalStepNames(STFlow flow){
        this.flow = flow;
        this.errors = new ArrayList<>();
    }
    public static IValidator getInstance(STFlow flow) {
        return new ValidateIllegalStepNames(flow);
    }
    @Override
    public Boolean validate() {
        List<String> legalNames = Arrays.stream(STEPS.values()).map(STEPS::name).collect(Collectors.toList());

        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow())
            if(!legalNames.contains(step.getName()))
                errors.add("Illegal step name: " + step.getName());

        if(!errors.isEmpty())
            return true;
        return false;
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }
}
