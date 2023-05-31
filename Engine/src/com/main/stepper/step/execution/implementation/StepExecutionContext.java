package com.main.stepper.step.execution.implementation;

import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.util.Map;
import java.util.UUID;

public class StepExecutionContext implements IStepExecutionContext {
    private UUID runId;
    private final Map<IDataIO, IDataIO> mapping;
    private final Map<IDataIO, Object> variables;
    private final ILogger logger;


    public StepExecutionContext(Map<IDataIO, Object> variables, Map<IDataIO, IDataIO> mapping, ILogger logger) {
        this.runId = UUID.randomUUID();
        this.variables = variables;
        this.mapping = mapping;
        this.logger = logger.getSubLogger(runId.toString());
    }

    @Override
    public String getUniqueRunId() {
        return runId.toString();
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

        if(variables.get(alias) == null)
            return null;

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
}
