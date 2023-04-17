package com.mta.java.stepper.data.implementation.numeric;

import com.mta.java.stepper.data.api.AbstractDataDef;

public class NumberDef extends AbstractDataDef {
    public NumberDef() {
        super("Number", true, Integer.class);
    }
}
