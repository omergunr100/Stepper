package com.main.stepper.shared.structures.gson.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.shared.structures.gson.adapters.IDataIOAdapterFactory;

public class GsonBuilderInterfaces {
    private static GsonBuilder builder;
    static {
        builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new IDataIOAdapterFactory(DataIO.class));
    }

    public static Gson create() {
        return builder.create();
    }
}
