package com.main.stepper.data.implementation.mapping;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.mapping.implementation.IntToIntPair;

public class IntToIntMappingDef extends AbstractDataDef {
    public IntToIntMappingDef() {
        super("Mapping", false, IntToIntPair.class);
    }
}
