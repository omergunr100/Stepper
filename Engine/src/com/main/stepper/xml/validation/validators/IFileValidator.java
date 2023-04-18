package com.main.stepper.xml.validation.validators;

import com.main.stepper.xml.flow.STFlows;

import java.util.List;

public interface IFileValidator {
    public static IFileValidator getInstance(STFlows flows){return null;};
    public Boolean validate();
    public List<String> getErrors();
}
