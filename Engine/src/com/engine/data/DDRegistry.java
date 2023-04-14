package com.engine.data;

import com.engine.data.api.IDataDefinition;
import com.engine.data.implementation.file.FileDef;
import com.engine.data.implementation.list.ListDef;
import com.engine.data.implementation.mapping.MappingDef;
import com.engine.data.implementation.numeric.DoubleDef;
import com.engine.data.implementation.numeric.NumberDef;
import com.engine.data.implementation.relation.RelationDef;
import com.engine.data.implementation.string.StringDef;

public enum DDRegistry implements IDataDefinition {
    STRING(new StringDef()),
    NUMBER(new NumberDef()),
    DOUBLE(new DoubleDef()),
    RELATION(new RelationDef()),
    LIST(new ListDef()),
    FILE(new FileDef()),
    MAPPING(new MappingDef())
    ;

    DDRegistry(IDataDefinition dataDef) {
        this.dataDef = dataDef;
    }

    private final IDataDefinition dataDef;

    @Override
    public String getName() {
        return dataDef.getName();
    }

    @Override
    public Boolean isUserFriendly() {
        return dataDef.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return dataDef.getType();
    }
}
