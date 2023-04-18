package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.xml.generated.STFlow;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.List;

public class ValidateNonExistantStepReferences implements IValidator {
    private STFlow flow;

    public ValidateNonExistantStepReferences(STFlow flow) {
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        // TODO: Implement this
        return null;
    }
}
