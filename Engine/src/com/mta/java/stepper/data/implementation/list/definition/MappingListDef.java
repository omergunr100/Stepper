package com.mta.java.stepper.data.implementation.list.definition;

import com.mta.java.stepper.data.api.AbstractDataDef;
import com.mta.java.stepper.data.implementation.list.datatype.MappingList;

public class MappingListDef extends AbstractDataDef {
    public MappingListDef() {
        super("Mapping List", false, MappingList.class);
    }
}
