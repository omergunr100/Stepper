package com.mta.java.stepper.data.api;

public interface IDataDefinition {
    String getName();
    Boolean isUserFriendly();
    Class<?> getType();
}

