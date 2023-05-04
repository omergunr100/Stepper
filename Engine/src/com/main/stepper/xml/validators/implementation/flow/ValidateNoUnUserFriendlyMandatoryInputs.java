package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.flow.definition.implementation.Flow;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidateNoUnUserFriendlyMandatoryInputs implements IValidator {
    private Flow flow;

    public ValidateNoUnUserFriendlyMandatoryInputs(Flow flow){
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        flow.userRequiredInputs().stream()
                .filter(input -> input.getNecessity().equals(DataNecessity.MANDATORY))
                .filter(input -> !input.getDataDefinition().isUserFriendly())
                .collect(Collectors.toList())
                .forEach(input -> {
                    errors.add("Input: " + input.getName() + " is mandatory and isn't user friendly in flow: " + flow.name());
                });

        return errors;
    }
}
