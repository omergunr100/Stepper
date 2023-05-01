package com.main.stepper.data.api;

import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;

public interface IDataDefinition {
    String getName();
    Boolean isUserFriendly();
    Class<?> getType();
    <T> T readValue(String data) throws BadTypeException, UnfriendlyInputException;
}

