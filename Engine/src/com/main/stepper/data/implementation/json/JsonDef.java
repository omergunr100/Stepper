package com.main.stepper.data.implementation.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.data.UnfriendlyInputException;

public class JsonDef extends AbstractDataDef {
    public JsonDef() {
        super("Json", true, JsonObject.class);
    }

    @Override
    public JsonObject readValue(String data) throws BadTypeException, UnfriendlyInputException {
        try {
            return JsonParser.parseString(data).getAsJsonObject();
        } catch (Exception e) {
            throw new BadTypeException("String isn't a valid Json object");
        }
    }
}
