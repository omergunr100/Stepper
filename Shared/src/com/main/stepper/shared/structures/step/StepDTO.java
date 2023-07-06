package com.main.stepper.shared.structures.step;

import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.util.ArrayList;
import java.util.List;

public class StepDTO {
    private String name;
    private String type;
    private String alias;
    private Boolean isReadOnly;
    private Boolean isSkipIfFailed;
    private ArrayList<DataIODTO> inputs;
    private ArrayList<DataIODTO> outputs;

    public StepDTO(String name, String type, String alias, Boolean isReadOnly, Boolean isSkipIfFailed, List<DataIODTO> inputs, List<DataIODTO> outputs) {
        this.name = name;
        this.type = type;
        this.alias = alias;
        this.isReadOnly = isReadOnly;
        this.isSkipIfFailed = isSkipIfFailed;
        this.inputs = new ArrayList<>(inputs);
        this.outputs = new ArrayList<>(outputs);
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public String alias() {
        return alias;
    }

    public Boolean isReadOnly() {
        return isReadOnly;
    }

    public Boolean isSkipIfFailed() {
        return isSkipIfFailed;
    }

    public ArrayList<DataIODTO> inputs() {
        return inputs;
    }

    public ArrayList<DataIODTO> outputs() {
        return outputs;
    }
}
