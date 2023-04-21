package com.main.stepper.data.implementation.numeric;

import com.main.stepper.data.api.AbstractDataDef;

public class NumberDef extends AbstractDataDef {
    public NumberDef() {
        super("Number", true, Integer.class);
    }

    @Override
    public Integer readValue(String data) {
        return Integer.valueOf(data);
    }
}
