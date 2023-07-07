package com.main.stepper.shared.structures.executionuserinputs;

import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.step.StepUsageDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExecutionUserInputsDTO {
    private ArrayList<DataIODTO> openUserInputs;
    private HashMap<DataIODTO, Object> userInputs;
    private HashMap<DataIODTO, StepUsageDTO> userInputsToSteps;

    public ExecutionUserInputsDTO(List<DataIODTO> openUserInputs, Map<DataIODTO, Object> userInputs) {
        this.openUserInputs = new ArrayList<>(openUserInputs);
        this.userInputs = new HashMap<>(userInputs);
        userInputsToSteps = new HashMap<>();
    }

    public List<DataIODTO> getOpenUserInputs() {
        return openUserInputs;
    }

    public Map<DataIODTO, Object> getUserInputs() {
        return userInputs;
    }

    public StepUsageDTO getStep(DataIODTO input){
        return userInputsToSteps.get(input);
    }

    public void readUserInput(DataIODTO input, String value) throws BadTypeException {
        try {
            userInputs.put(input, input.type().readValue(value));
        } catch (UnfriendlyInputException ignored) {
        } catch (BadTypeException e) {
            userInputs.put(input, null);
            throw e;
        }
    }

    public Boolean isFilled(DataIODTO input) {
        if(userInputs.containsKey(input) && userInputs.get(input) != null)
            return true;
        return false;
    }

    public Boolean validateUserInputs() {
        List<DataIODTO> mandatory = openUserInputs.stream().filter(data -> data.necessity().equals(DataNecessity.MANDATORY)).collect(Collectors.toList());
        for (DataIODTO input : mandatory) {
            if (!isFilled(input))
                return false;
        }
        return true;
    }
}
