package com.main.stepper.shared.structures.step;

import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StepDefinitionDTO {
    private String name;
    private Boolean isReadOnly;
    private ArrayList<DataIODTO> inputs;
    private ArrayList<DataIODTO> outputs;

    public StepDefinitionDTO(String name, Boolean isReadOnly, List<DataIODTO> inputs, List<DataIODTO> outputs) {
        this.name = name;
        this.isReadOnly = isReadOnly;
        this.inputs = new ArrayList<>(inputs);
        this.outputs = new ArrayList<>(outputs);
    }

    public String name() {
        return name;
    }

    public Boolean isReadOnly() {
        return isReadOnly;
    }

    public List<DataIODTO> inputs() {
        return inputs;
    }

    public List<DataIODTO> outputs() {
        return outputs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepDefinitionDTO that = (StepDefinitionDTO) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
