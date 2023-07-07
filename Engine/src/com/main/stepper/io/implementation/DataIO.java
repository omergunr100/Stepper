package com.main.stepper.io.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.api.IDataDefinition;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.util.Arrays;

public class DataIO implements IDataIO {
    private final String name;
    private final String userString;
    private final DataNecessity necessity;
    private final IDataDefinition dataDefinition;

    public DataIO(String name, String userString, DataNecessity necessity, IDataDefinition dataDefinition) {
        this.name = name;
        this.userString = userString;
        this.necessity = necessity;
        this.dataDefinition = dataDefinition;
    }

    public DataIO(String name, String userString, IDataDefinition dataDefinition){
        this(name, userString, DataNecessity.NA, dataDefinition);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUserString() {
        return userString;
    }

    @Override
    public IDataDefinition getDataDefinition() {
        return dataDefinition;
    }

    @Override
    public DataNecessity getNecessity() {
        return necessity;
    }

    @Override
    public DataIODTO toDTO() {
        return new DataIODTO(
                name,
                userString,
                Arrays.stream(DDRegistry.values()).filter(dd -> dd.getType().equals(dataDefinition.getType())).findFirst().get(),
                necessity
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataIO dataIO = (DataIO) o;

        return name.equals(dataIO.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(String o) {
        if(o == null) {
            return 1;
        }
        return name.compareTo(o);
    }

    @Override
    public String toString() {
        return "DataIO{" +
                "name='" + name + '\'' +
                ", userString='" + userString + '\'' +
                ", necessity=" + necessity +
                ", dataDefinition=" + dataDefinition.getType().getSimpleName() +
                '}';
    }
}
