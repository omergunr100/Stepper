package com.main.stepper.data;

import com.main.stepper.data.api.IDataDefinition;
import com.main.stepper.data.implementation.file.FileDef;
import com.main.stepper.data.implementation.list.definition.*;
import com.main.stepper.data.implementation.mapping.IntToIntMappingDef;
import com.main.stepper.data.implementation.mapping.MappingDef;
import com.main.stepper.data.implementation.numeric.DoubleDef;
import com.main.stepper.data.implementation.numeric.NumberDef;
import com.main.stepper.data.implementation.relation.RelationDef;
import com.main.stepper.data.implementation.string.StringDef;
import com.main.stepper.exceptions.data.BadReadException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;

public enum DDRegistry implements IDataDefinition {
    STRING(new StringDef()),
    NUMBER(new NumberDef()),
    DOUBLE(new DoubleDef()),
    RELATION(new RelationDef()),
    FILE(new FileDef()),
    INT_TO_INT_MAPPING(new IntToIntMappingDef()),
    STRING_LIST(new StringListDef()),
    RELATION_LIST(new RelationListDef()),
    NUMBER_LIST(new NumberListDef()),
    MAPPING_LIST(new MappingListDef()), // TODO: maybe delete if becomes problematic due to many mapping types
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

    @Override
    public <T> T readValue(String data) throws BadReadException, UnfriendlyInputException {
        return dataDef.readValue(data);
    }
}
