package com.main.stepper.data.implementation.enumeration.httpprotocol;

import com.main.stepper.exceptions.data.EnumValueMissingException;

import java.util.*;

public class HttpProtocolEnumData {
    private static List<String> values = Collections.synchronizedList(Arrays.asList("http", "https"));
    private String value;

    public HttpProtocolEnumData(){
        this.value = null;
    }

    public static List<String> getValues() {
        return new ArrayList<>(values);
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public void setValue(String value) throws EnumValueMissingException {
        if(!values.contains(value))
            throw new EnumValueMissingException("Invalid value for enumerator!");
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
