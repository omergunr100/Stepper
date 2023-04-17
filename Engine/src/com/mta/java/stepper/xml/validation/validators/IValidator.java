package com.mta.java.stepper.xml.validation.validators;

import com.mta.java.stepper.xml.flow.STFlow;

import java.util.List;

public interface IValidator {
    public static IValidator getInstance(STFlow flow){ return null; };
    public Boolean validate();
    public List<String> getErrors();
}
