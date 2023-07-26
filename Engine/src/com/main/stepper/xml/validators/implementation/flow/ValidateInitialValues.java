package com.main.stepper.xml.validators.implementation.flow;

import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.xml.validators.api.IValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidateInitialValues implements IValidator {
    private IFlowDefinition flow;

    public ValidateInitialValues(IFlowDefinition flow){
        this.flow = flow;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        List<IDataIO> allInputs = new ArrayList<>();
        allInputs.addAll(flow.userRequiredInputs());
        allInputs.addAll(flow.userOptionalInputs());

        if (!flow.initialValuesRaw().isEmpty()) {
            for (Map.Entry<String, String> entry : flow.initialValuesRaw().entrySet()) {
                Optional<IDataIO> match = allInputs.stream().filter(input -> input.getName().equals(entry.getKey())).findFirst();
                if (!match.isPresent()) {
                    errors.add("Input: " + entry.getKey() + " is missing in flow: " + flow.name() + ", even though it has an assigned initial value.");
                }
                else {
                    try {
                        // try and match key to an actual open input
                        Object initial = match.get().getDataDefinition().readValue(entry.getValue());
                        flow.addInitialValue(match.get(), initial);
                        // if found make sure it isn't open
                        flow.userRequiredInputs().removeIf(open -> open.equals(match.get()));
                        flow.userOptionalInputs().removeIf(open -> open.equals(match.get()));
                    } catch (BadTypeException e) {
                        errors.add("Input: " + entry.getKey() + " has assigned initial value with a type missmatch in flow: " + flow.name() + ".");
                    } catch (UnfriendlyInputException e) {
                        errors.add("Input: " + entry.getKey() + " has assigned initial value in flow: " + flow.name() + ", even though it isn't user friendly.");
                    }
                }
            }
        }

        return errors;
    }
}
