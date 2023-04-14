package com.engine.io.implementation;

import com.engine.data.api.IDataDefinition;
import com.engine.io.api.DataNecessity;
import com.engine.io.api.IDataIO;

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
}
