package com.mta.java.stepper.data;

import com.mta.java.stepper.data.api.IDataDefinition;
import com.mta.java.stepper.data.implementation.file.FileDef;
import com.mta.java.stepper.data.implementation.list.definition.*;
import com.mta.java.stepper.data.implementation.mapping.MappingDef;
import com.mta.java.stepper.data.implementation.numeric.DoubleDef;
import com.mta.java.stepper.data.implementation.numeric.NumberDef;
import com.mta.java.stepper.data.implementation.relation.RelationDef;
import com.mta.java.stepper.data.implementation.string.StringDef;

public enum DDRegistry implements IDataDefinition {
    STRING(new StringDef()),
    NUMBER(new NumberDef()),
    DOUBLE(new DoubleDef()),
    RELATION(new RelationDef()),
    FILE(new FileDef()),
    MAPPING(new MappingDef()),
    STRING_LIST(new StringListDef()),
    RELATION_LIST(new RelationListDef()),
    NUMBER_LIST(new NumberListDef()),
    MAPPING_LIST(new MappingListDef()),
    FILE_LIST(new FileListDef()),
    DOUBLE_LIST(new DoubleListDef())
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
