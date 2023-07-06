package com.main.stepper.shared.structures.dataio;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.io.api.DataNecessity;

import java.util.Objects;
import java.util.Optional;

public class DataIODTO {
    private String name;
    private String userString;
    private DDRegistry type;
    private DataNecessity necessity;
    private Object value;

    public DataIODTO(String name, String userString, DDRegistry type, DataNecessity necessity, Object value) {
        this.name = name;
        this.userString = userString;
        this.type = type;
        this.necessity = necessity;
        this.value = value;
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

    public Optional<Object> value() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataIODTO dataIODTO = (DataIODTO) o;

        if (!Objects.equals(name, dataIODTO.name)) return false;
        if (!Objects.equals(userString, dataIODTO.userString)) return false;
        if (type != dataIODTO.type) return false;
        return necessity == dataIODTO.necessity;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (userString != null ? userString.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (necessity != null ? necessity.hashCode() : 0);
        return result;
    }
}
