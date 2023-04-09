package data.io;

import data.definitions.DataDef;

import java.util.Objects;

public abstract class IO<T extends DataDef> {
    // The required data definition for the IO
    protected Class<T> requiredDataType;
    // The data definition for the IO
    protected T data;
    // The alias for the IO
    protected String alias;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof IO)) return false;
        IO io = (IO) o;
        return Objects.equals(alias, io.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }

    // The user string for the IO
    protected String userString;

    public IO(Class<T> requiredDataType, String alias, String userString){
        this.requiredDataType=requiredDataType;
        this.alias=alias;
        this.userString=userString;
        this.data = null;
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
}

