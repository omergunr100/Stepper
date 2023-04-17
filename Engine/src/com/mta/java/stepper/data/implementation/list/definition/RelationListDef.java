package com.mta.java.stepper.data.implementation.list.definition;

import com.mta.java.stepper.data.api.AbstractDataDef;
import com.mta.java.stepper.data.implementation.list.datatype.RelationList;

public class RelationListDef extends AbstractDataDef {
    public RelationListDef() {
        super("Relation List", false, RelationList.class);
    }
}
