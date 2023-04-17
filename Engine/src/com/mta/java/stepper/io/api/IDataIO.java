package com.mta.java.stepper.io.api;

import com.mta.java.stepper.data.api.IDataDefinition;

public interface IDataIO extends Comparable<String> {
    String getName();
    String getUserString();
    IDataDefinition getDataDefinition();
    DataNecessity getNecessity();
}
