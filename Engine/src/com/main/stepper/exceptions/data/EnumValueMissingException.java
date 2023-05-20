package com.main.stepper.exceptions.data;

public class EnumValueMissingException extends Exception{
    public EnumValueMissingException(String message) {
        super(message);
    }

    public EnumValueMissingException(Exception e) {
        super(e);
    }
}
