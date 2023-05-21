package com.main.stepper.data.implementation.enumeration.zipper;

import com.main.stepper.data.implementation.enumeration.api.IHasValues;
import com.main.stepper.exceptions.data.EnumValueMissingException;

import java.util.*;

public class ZipperEnumData {
    private static List<String> values = Collections.synchronizedList(Arrays.asList("ZIP", "UNZIP"));
    private String value;

    public ZipperEnumData(){
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
        return "Value: " + value;
    }
}
