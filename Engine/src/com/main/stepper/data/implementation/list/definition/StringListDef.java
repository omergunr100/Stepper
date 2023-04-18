package com.main.stepper.data.implementation.list.definition;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.list.datatype.StringList;

public class StringListDef extends AbstractDataDef {
    public StringListDef() {
        super("String List", false, StringList.class);
    }
}
