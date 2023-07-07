package com.main.stepper.shared.structures.dataio;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.io.api.DataNecessity;

import java.util.Objects;

public class DataIODTO {
    private String name;
    private String userString;
    private DDRegistry type;
    private DataNecessity necessity;

    public DataIODTO(String name, String userString, DDRegistry type, DataNecessity necessity) {
        this.name = name;
        this.userString = userString;
        this.type = type;
        this.necessity = necessity;
    }

    public String name() {
        return name;
    }

    public String userString() {
        return userString;
    }

    public DDRegistry type() {
        return type;
    }

    public DataNecessity necessity() {
        return necessity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataIODTO dataIODTO = (DataIODTO) o;

        return Objects.equals(name, dataIODTO.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
