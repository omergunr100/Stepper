package com.main.stepper.io.api;

import com.main.stepper.data.api.IDataDefinition;
import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.io.Serializable;

public interface IDataIO extends Comparable<String>, Serializable {
    String getName();
    String getUserString();
    IDataDefinition getDataDefinition();
    DataNecessity getNecessity();
    DataIODTO toDTO();
}
