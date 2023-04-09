package data.io;

import data.definitions.DataDef;

public class Input<T extends DataDef> extends IO<T>{
    // The default value for the input
    protected T defaultValue;
    // Whether the input is required
    protected Boolean required;
    public Input(Class<T> requiredDataType, T defaultValue, String alias, String userString, Boolean required){
        super(requiredDataType, alias, userString);
        this.defaultValue=defaultValue;
        this.required=required;
    }
    public Input(Class<T> requiredDataType, String alias, String userString, Boolean required){
        super(requiredDataType, alias, userString);
        this.defaultValue=null;
        this.required=required;
    }
    public Boolean isRequired() {
        return required;
    }
    public T getDefaultValue() {
        return defaultValue;
    }
}
