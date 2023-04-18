package com.main.stepper.data.implementation.list.definition;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.list.datatype.DoubleList;

public class DoubleListDef extends AbstractDataDef {
    public DoubleListDef() {
        super("Double List", false, DoubleList.class);
    }
}
