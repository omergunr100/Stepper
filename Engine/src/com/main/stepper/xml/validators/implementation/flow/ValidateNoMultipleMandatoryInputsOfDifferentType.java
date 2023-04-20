package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.definition.implementation.Flow;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidateNoMultipleMandatoryInputsOfDifferentType implements IValidator {
    private Flow flow;

    public ValidateNoMultipleMandatoryInputsOfDifferentType(Flow flow) {
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        for(IDataIO input : flow.userRequiredInputs()) {
            Class<?> type = input.getDataDefinition().getType();

            for (IStepUsageDeclaration step : flow.steps())
                for (IDataIO stepInput : step.step().getInputs())
                    if (flow.mapsTo(step, stepInput).equals(input))
                        if (!stepInput.getDataDefinition().getType().equals(type))
                            errors.add("Step " + step.step().getName() + " has a mandatory input of type " + stepInput.getDataDefinition().getType().getSimpleName() + " which conflicts with the type of the mandatory input '" + input.getName() + "' of type " + type.getSimpleName() + " in the flow " + flow.name());
        }

        return errors;
    }
}
