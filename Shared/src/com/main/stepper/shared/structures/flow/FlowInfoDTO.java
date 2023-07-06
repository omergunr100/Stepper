package com.main.stepper.shared.structures.flow;

import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.step.StepDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowInfoDTO {
    private String name;
    private String description;
    private ArrayList<StepDTO> steps;
    private ArrayList<DataIODTO> openUserInputs;
    private ArrayList<String> continuations;
    private Boolean isReadOnly;
    private ArrayList<DataIODTO> formalOutputs;
    private HashMap<DataIODTO, ArrayList<StepDTO>> dataToConsumer;
    private HashMap<DataIODTO, StepDTO> dataToProducer;
    private ArrayList<DataIODTO> internalOutputs;

    public FlowInfoDTO(String name, String description, List<StepDTO> steps, List<DataIODTO> openUserInputs, List<String> continuations, Boolean isReadOnly, List<DataIODTO> formalOutputs, Map<DataIODTO, List<StepDTO>> dataToConsumer, Map<DataIODTO, StepDTO> dataToProducer, List<DataIODTO> internalOutputs) {
        this.name = name;
        this.description = description;
        this.steps = new ArrayList<>(steps);
        this.openUserInputs = new ArrayList<>(openUserInputs);
        this.continuations = new ArrayList<>(continuations);
        this.isReadOnly = isReadOnly;
        this.formalOutputs = new ArrayList<>(formalOutputs);
        this.dataToConsumer = new HashMap<>();
        for (DataIODTO dataIODTO : dataToConsumer.keySet()) {
            this.dataToConsumer.put(dataIODTO, new ArrayList<>(dataToConsumer.get(dataIODTO)));
        }
        this.dataToProducer = new HashMap<>(dataToProducer);
        this.internalOutputs = new ArrayList<>(internalOutputs);
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public ArrayList<StepDTO> steps() {
        return steps;
    }

    public ArrayList<DataIODTO> openUserInputs() {
        return openUserInputs;
    }

    public ArrayList<String> continuations() {
        return continuations;
    }

    public Boolean isReadOnly() {
        return isReadOnly;
    }

    public ArrayList<DataIODTO> formalOutputs() {
        return formalOutputs;
    }

    public ArrayList<StepDTO> linkedSteps(DataIODTO dataIODTO) {
        return dataToConsumer.get(dataIODTO);
    }

    public StepDTO producerStep(DataIODTO dataIODTO) {
        return dataToProducer.get(dataIODTO);
    }

    public ArrayList<DataIODTO> internalOutputs() {
        return internalOutputs;
    }
}
