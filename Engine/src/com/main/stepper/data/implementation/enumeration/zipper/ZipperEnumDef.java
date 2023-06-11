package com.main.stepper.data.implementation.enumeration.zipper;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.EnumValueMissingException;

public class ZipperEnumDef extends AbstractDataDef {
    public ZipperEnumDef() {
        super("ZIP Enumerator", true, ZipperEnumData.class);
    }

    @Override
    public ZipperEnumData readValue(String data) throws BadTypeException {
        ZipperEnumData enumData = new ZipperEnumData();
        try {
            enumData.setValue(data);
        } catch (EnumValueMissingException e) {
            throw new BadTypeException(e);
        }
        return enumData;
    }
}
