package com.main.stepper.shared.structures.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.io.api.IDataIO;

public class IDataIOAdapterFactory implements TypeAdapterFactory {
    private Class<? extends IDataIO> implementationClass;

    public IDataIOAdapterFactory(Class<? extends IDataIO> implementationClass) {
        this.implementationClass = implementationClass;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!IDataIO.class.equals(type.getRawType())) return null;

        return (TypeAdapter<T>) gson.getAdapter(implementationClass);
    }
}
