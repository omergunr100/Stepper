package com.engine.step.implementation;

import com.engine.data.api.IDataDefinition;
import com.engine.logger.Logger;
import com.engine.step.api.IStepExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StepExecutionContext implements IStepExecutionContext {
    private String summary;
    private final Map<String, Object> variables;
    private final Logger logger;

    public StepExecutionContext(Map<String, Object> variables, Logger logger) {
        this.variables = variables;
        this.logger = logger;
    }

    @Override
    public void Log(String message) {
        logger.log(message);
    }

    @Override
    public <T> T getInput(String name, IDataDefinition type) {
        if(!variables.containsKey(name)){
            Log("Variable " + name + " not found in context");
            return null;
        }

        if(!type.getType().isAssignableFrom(variables.get(name).getClass())){
            Log("Variable " + name + " is not of type " + type.getName());
            return null;
        }
        return (T) type.getType().cast(variables.get(name));
    }

    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String getSummary() {
        return summary;
    }
}
