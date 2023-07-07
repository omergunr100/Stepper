package com.main.stepper.step.execution.implementation;

import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.shared.structures.step.StepExecutionContextDTO;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public UUID getUniqueRunId() {
        return runId;
    }

    @Override
    public void log(String message) {
        logger.log(message);
    }

    @Override
    public List<Log> getLogs() {
        return logger.getLog();
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

    @Override
    public IDataIO getAliasedDataIO(IDataIO dataIO) {
        return mapping.get(dataIO);
    }

    @Override
    public StepExecutionContextDTO toDTO() {
        return new StepExecutionContextDTO(
                runId,
                mapping.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toDTO(), entry -> entry.getValue().toDTO())),
                variables.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toDTO(), Map.Entry::getValue)),
                logger
        );
    }
}
