package com.main.stepper.data.implementation.mapping;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.mapping.api.PairData;

public class MappingDef extends AbstractDataDef {
    public MappingDef() {
        super("Mapping", false, PairData.class);
    }
}
