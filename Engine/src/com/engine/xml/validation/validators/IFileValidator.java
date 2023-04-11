package com.engine.xml.validation.validators;

import com.engine.xml.flow.STFlows;

import java.util.List;

public interface IFileValidator {
    public static IFileValidator getInstance(STFlows flows){return null;};
    public Boolean validate();
    public List<String> getErrors();
}
