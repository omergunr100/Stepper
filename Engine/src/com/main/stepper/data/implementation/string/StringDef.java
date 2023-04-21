package com.main.stepper.data.implementation.string;

import com.main.stepper.data.api.AbstractDataDef;

public class StringDef extends AbstractDataDef {
    public StringDef() {
        super("String", true, String.class);
    }

    @Override
    public String readValue(String data) {
        return data;
    }
}
