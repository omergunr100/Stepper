package com.main.stepper.data.api;

import com.main.stepper.exceptions.data.BadReadException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;

public abstract class AbstractDataDef implements IDataDefinition {
    private String name;
    private Boolean userFriendly;
    private Class<?> type;

    protected AbstractDataDef(String name, Boolean userFriendly, Class<?> type){
        this.name = name;
        this.userFriendly = userFriendly;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean isUserFriendly() {
        return userFriendly;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public <T> T readValue(String data) throws BadReadException, UnfriendlyInputException {
        throw new UnfriendlyInputException("This data definition is not user friendly.");
    }
}
