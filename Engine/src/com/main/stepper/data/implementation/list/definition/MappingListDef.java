package com.main.stepper.data.implementation.list.definition;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.list.datatype.MappingList;

public class MappingListDef extends AbstractDataDef {
    public MappingListDef() {
        super("Mapping List", false, MappingList.class);
    }
}
