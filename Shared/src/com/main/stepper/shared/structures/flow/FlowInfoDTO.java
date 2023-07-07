package com.main.stepper.shared.structures.flow;

import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.step.StepUsageDTO;

import java.util.*;

public class FlowInfoDTO {
    private String name;
    private String description;
    private ArrayList<StepUsageDTO> steps;
    private ArrayList<DataIODTO> openUserInputs;
    private ArrayList<String> continuations;
    private Boolean isReadOnly;
    private ArrayList<DataIODTO> formalOutputs;
    private HashMap<DataIODTO, ArrayList<StepUsageDTO>> dataToConsumer;
    private HashMap<DataIODTO, StepUsageDTO> dataToProducer;
    private ArrayList<DataIODTO> internalOutputs;

    public FlowInfoDTO(String name, String description, List<StepUsageDTO> steps, List<DataIODTO> openUserInputs, List<String> continuations, Boolean isReadOnly, List<DataIODTO> formalOutputs, Map<DataIODTO, List<StepUsageDTO>> dataToConsumer, Map<DataIODTO, StepUsageDTO> dataToProducer, List<DataIODTO> internalOutputs) {
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

    public ArrayList<StepUsageDTO> steps() {
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

    public ArrayList<StepUsageDTO> linkedSteps(DataIODTO dataIODTO) {
        return dataToConsumer.get(dataIODTO);
    }

    public StepUsageDTO producerStep(DataIODTO dataIODTO) {
        return dataToProducer.get(dataIODTO);
    }

    public ArrayList<DataIODTO> internalOutputs() {
        return internalOutputs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowInfoDTO that = (FlowInfoDTO) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
