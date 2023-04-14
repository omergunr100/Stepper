package com.engine.data.implementation.numeric;

import com.engine.data.api.AbstractDataDef;

public class NumberDef extends AbstractDataDef {
    public NumberDef() {
        super("Number", true, Integer.class);
    }
}
