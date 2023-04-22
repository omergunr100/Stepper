package com.main.stepper.data.api;

import com.main.stepper.exceptions.data.BadReadException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;

import java.io.InputStream;

public interface IDataDefinition {
    String getName();
    Boolean isUserFriendly();
    Class<?> getType();
    <T> T readValue(String data) throws BadReadException, UnfriendlyInputException;
}

