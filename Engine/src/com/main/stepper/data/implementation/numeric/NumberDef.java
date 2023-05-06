package com.main.stepper.data.implementation.numeric;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.exceptions.data.BadTypeException;

public class NumberDef extends AbstractDataDef {
    public NumberDef() {
        super("Number", true, Integer.class);
    }

    @Override
    public Integer readValue(String data) throws BadTypeException {
        try{
            return Integer.valueOf(data);
        } catch (NumberFormatException e){
            throw new BadTypeException(e);
        }
    }
}
