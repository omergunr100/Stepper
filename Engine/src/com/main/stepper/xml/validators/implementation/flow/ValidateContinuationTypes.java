package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.List;

public class ValidateContinuationTypes implements IValidator {
    private IFlowDefinition flow;

    public ValidateContinuationTypes(IFlowDefinition flow) {
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // for each continuation this flow has, check if data types match required and are inputs
        for(IFlowDefinition continuation : flow.continuations()) {
            // check if mapping to input
            // check if mapping to same data type
        }

        return errors;
    }
}
