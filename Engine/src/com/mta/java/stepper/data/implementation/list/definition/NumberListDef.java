package com.mta.java.stepper.data.implementation.list.definition;

import com.mta.java.stepper.data.api.AbstractDataDef;
import com.mta.java.stepper.data.implementation.list.datatype.NumberList;

public class NumberListDef extends AbstractDataDef {
    public NumberListDef() {
        super("Number List", false, NumberList.class);
    }
}
