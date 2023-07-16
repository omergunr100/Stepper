package com.main.stepper.data.implementation.enumeration.httpverb;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.EnumValueMissingException;

public class HttpVerbEnumDef extends AbstractDataDef {
    public HttpVerbEnumDef() {
        super("Http Verb Enumerator", true, HttpVerbEnumData.class);
    }

    @Override
    public HttpVerbEnumData readValue(String data) throws BadTypeException {
        HttpVerbEnumData enumData = new HttpVerbEnumData();
        try {
            enumData.setValue(data);
        } catch (EnumValueMissingException e) {
            throw new BadTypeException(e);
        }
        return enumData;
    }
}
