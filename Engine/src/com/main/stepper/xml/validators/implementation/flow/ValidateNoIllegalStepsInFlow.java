package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.step.definition.StepRegistry;
import com.main.stepper.xml.generated.STStepInFlow;
import com.main.stepper.xml.generated.STFlow;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidateNoIllegalStepsInFlow implements IValidator {
    private STFlow flow;

    public ValidateNoIllegalStepsInFlow(STFlow flow){
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        List<String> legalNames = Arrays.stream(StepRegistry.values()).map(StepRegistry::name).collect(Collectors.toList());

        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow())
            if(!legalNames.contains(step.getName()))
                errors.add("Illegal step name: " + step.getName() + ", in flow: " + flow.getName());

        return errors;
    }
}
