package com.mta.java.stepper.step.execution.implementation;

import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.logger.api.ILogger;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;

import java.util.Map;

public class StepExecutionContext implements IStepExecutionContext {
    private String summary;
    private final Map<IDataIO, IDataIO> mapping;
    private final Map<IDataIO, Object> variables;
    private final ILogger logger;

    public StepExecutionContext(Map<IDataIO, Object> variables, Map<IDataIO, IDataIO> mapping, ILogger logger) {
        this.variables = variables;
        this.mapping = mapping;
        this.logger = logger;
    }

    @Override
    public void log(String message) {
        logger.log(message);
    }

    @Override
    public <T> T getInput(IDataIO name, Class<T> type) {
        IDataIO alias = mapping.get(name);

        if(!variables.containsKey(alias)){
            log("Variable " + alias.getName() + " not found in context");
            return null;
        }

        if(!type.isAssignableFrom(variables.get(alias).getClass())){
            log("Variable " + alias.getName() + " is not of type " + type.getName());
            return null;
        }
        return type.cast(variables.get(alias));
    }

    @Override
    public void setOutput(IDataIO name, Object value) {
        IDataIO alias = mapping.get(name);
        variables.put(alias, value);
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
