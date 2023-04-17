package com.engine.deprecated.data.io;

import com.engine.deprecated.data.definitions.DataDef;

import java.util.Objects;

public class Output<T extends DataDef> extends IO<T>{
    public Output(Class<T> requiredDataType, String alias, String userString){
        super(requiredDataType, alias, userString);
    }

    public Class<T> getType() {
        return requiredDataType;
    }
    public String getAlias() {
        return alias;
    }
    public String getUserString() {
        return userString;
    }
    public T getData() {
        return data;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Output output = (Output) o;
        return Objects.equals(alias, output.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }
}
