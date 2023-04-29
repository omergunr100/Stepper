package com.main.stepper.exceptions;

import com.main.stepper.exceptions.xml.XMLException;

import java.util.ArrayList;
import java.util.List;

public class ErrorListException extends Exception {
    private List<Exception> errors;

    public ErrorListException(String message) {
        super(message);
        errors = new ArrayList<>();
    }

    public ErrorListException(String message, List<Exception> errors){
        super(message);
        this.errors = errors;
    }

    public List<Exception> getErrors() {
        return errors;
    }
}
