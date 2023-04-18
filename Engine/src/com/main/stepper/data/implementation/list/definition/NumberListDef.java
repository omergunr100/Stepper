package com.main.stepper.data.implementation.list.definition;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.list.datatype.NumberList;

public class NumberListDef extends AbstractDataDef {
    public NumberListDef() {
        super("Number List", false, NumberList.class);
    }
}
