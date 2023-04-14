package com.engine.data.implementation.relation;

import com.engine.data.api.AbstractDataDef;

public class RelationDef extends AbstractDataDef {

    public RelationDef() {
        super("Relation", false, RelationData.class);
    }
}
