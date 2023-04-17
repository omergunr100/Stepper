package com.mta.java.stepper.flow.execution.api;

import com.mta.java.stepper.flow.definition.api.IStepUsageDeclaration;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.logger.api.ILogger;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;

public interface IFlowExecutionContext {
    String getUniqueRunId();
    ILogger getLogger();
    IDataIO getVariableIOByName(String name);
    <T> T getVariable(IDataIO name, Class<T> type);
    void setVariable(IDataIO name, Object value);
    IStepExecutionContext getStepExecutionContext(IStepUsageDeclaration step);
}
