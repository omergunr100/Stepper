package com.mta.java.stepper.data.implementation.list.definition;

import com.mta.java.stepper.data.api.AbstractDataDef;
import com.mta.java.stepper.data.implementation.list.datatype.StringList;

public class StringListDef extends AbstractDataDef {
    public StringListDef() {
        super("String List", false, StringList.class);
    }
}
