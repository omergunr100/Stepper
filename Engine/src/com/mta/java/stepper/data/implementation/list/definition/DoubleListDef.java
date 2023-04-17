package com.mta.java.stepper.data.implementation.list.definition;

import com.mta.java.stepper.data.api.AbstractDataDef;
import com.mta.java.stepper.data.implementation.list.datatype.DoubleList;

public class DoubleListDef extends AbstractDataDef {
    public DoubleListDef() {
        super("Double List", false, DoubleList.class);
    }
}
