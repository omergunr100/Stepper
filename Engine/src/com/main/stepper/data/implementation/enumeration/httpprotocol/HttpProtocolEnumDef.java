package com.main.stepper.data.implementation.enumeration.httpprotocol;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.enumeration.httpverb.HttpVerbEnumData;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.EnumValueMissingException;

public class HttpProtocolEnumDef extends AbstractDataDef {
    public HttpProtocolEnumDef() {
        super("Http Protocol Enumerator", true, HttpProtocolEnumData.class);
    }

    @Override
    public HttpProtocolEnumData readValue(String data) throws BadTypeException {
        HttpProtocolEnumData enumData = new HttpProtocolEnumData();
        try {
            enumData.setValue(data);
        } catch (EnumValueMissingException e) {
            throw new BadTypeException(e);
        }
        return enumData;
    }
}
