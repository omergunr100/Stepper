package com.main.stepper.data.api;

import java.io.InputStream;

public interface IDataDefinition {
    String getName();
    Boolean isUserFriendly();
    Class<?> getType();
    default <T> T readValue(String data) {return null;};
}

