package com.engine.data.api;

public interface IDataDefinition {
    String getName();
    Boolean isUserFriendly();
    Class<?> getType();
}

