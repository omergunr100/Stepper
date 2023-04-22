package com.main.stepper.flow.execution.implementation;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.step.execution.api.IStepExecutionContext;
import com.main.stepper.step.execution.implementation.StepExecutionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FlowExecutionContext implements IFlowExecutionContext {
    private final UUID uniqueRunId;
    private final ILogger logger;
    private final Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mappings;
    private final Map<IDataIO, Object> variables;

    public FlowExecutionContext(Map<IStepUsageDeclaration, Map<IDataIO, IDataIO>> mappings, ILogger logger) {
        uniqueRunId = UUID.randomUUID();
        this.logger = logger.getSubLogger(uniqueRunId.toString());
        this.mappings = mappings;
        variables = new HashMap<>();
    }

    @Override
    public String getUniqueRunId() {
        return uniqueRunId.toString();
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    @Override
    public IDataIO getVariableIOByName(String name) {
        Optional<IDataIO> io = variables.keySet().stream().filter(dataIO -> dataIO.getName().equals(name)).findFirst();
        return io.orElse(null);
    }

    @Override
    public <T> T getVariable(IDataIO name, Class<T> type) {
        return type.cast(variables.get(name));
    }

    @Override
    public void setVariable(IDataIO name, Object value) {
        variables.put(name, value);
    }

    @Override
    public IStepExecutionContext getStepExecutionContext(IStepUsageDeclaration step) {
        return new StepExecutionContext(variables, mappings.get(step), logger);
    }
}
