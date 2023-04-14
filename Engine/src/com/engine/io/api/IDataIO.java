package com.engine.io.api;

import com.engine.data.api.IDataDefinition;

public interface IDataIO {
    String getName();
    String getUserString();
    IDataDefinition getDataDefinition();
    DataNecessity getNecessity();
}
