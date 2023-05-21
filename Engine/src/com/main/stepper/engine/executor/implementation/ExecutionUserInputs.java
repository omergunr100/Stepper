package com.main.stepper.engine.executor.implementation;

import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExecutionUserInputs {
    private List<IDataIO> openUserInputs;
    private Map<IDataIO, Object> userInputs;
    private Map<IDataIO, IStepUsageDeclaration> userInputsToSteps;

    public ExecutionUserInputs(List<IDataIO> openUserInputs, Map<IDataIO, IStepUsageDeclaration> userInputsToSteps) {
        this.openUserInputs = openUserInputs;
        this.userInputsToSteps = userInputsToSteps;
        userInputs = new HashMap<>();
    }

    public List<IDataIO> getOpenUserInputs() {
        return openUserInputs;
    }

    public Map<IDataIO, Object> getUserInputs() {
        return userInputs;
    }

    public IStepUsageDeclaration getStep(IDataIO input){
        return userInputsToSteps.get(input);
    }

    public void readUserInput(IDataIO input, String value) throws BadTypeException {
        try {
            userInputs.put(input, input.getDataDefinition().readValue(value));
        } catch (UnfriendlyInputException ignored) {
        } catch (BadTypeException e) {
            userInputs.put(input, null);
            throw e;
        }
    }

    public Boolean isFilled(IDataIO input) {
        if(userInputs.containsKey(input) && userInputs.get(input) != null)
            return true;
        return false;
    }

    public Boolean validateUserInputs() {
        List<IDataIO> mandatory = openUserInputs.stream().filter(data -> data.getNecessity().equals(DataNecessity.MANDATORY)).collect(Collectors.toList());
        for (IDataIO input : mandatory) {
            if (!isFilled(input))
                return false;
        }
        return true;
    }
}
