package com.main.stepper.exceptions.data;

public class BadTypeException extends Exception{
    public BadTypeException(String message) {
        super(message);
    }

    public BadTypeException(Exception e) {
        super(e);
    }
}
