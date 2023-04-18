package com.main.stepper.io.api;

import com.main.stepper.data.api.IDataDefinition;

public interface IDataIO extends Comparable<String> {
    String getName();
    String getUserString();
    IDataDefinition getDataDefinition();
    DataNecessity getNecessity();
}
