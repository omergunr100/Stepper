package com.main.stepper.data.implementation.list.definition;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.list.datatype.RelationList;

public class RelationListDef extends AbstractDataDef {
    public RelationListDef() {
        super("Relation List", false, RelationList.class);
    }
}
