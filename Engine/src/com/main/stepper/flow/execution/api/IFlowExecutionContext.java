package com.main.stepper.flow.execution.api;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.util.Map;
import java.util.UUID;

public interface IFlowExecutionContext {
    UUID getUniqueRunId();
    ILogger getLogger();
    IDataIO getVariableIOByName(String name);
    <T> T getVariable(IDataIO name, Class<T> type);
    void setVariable(IDataIO name, Object value);
    IStepExecutionContext getStepExecutionContext(IStepUsageDeclaration step);
    Map<IDataIO, Object> variables();
    void addStepRunResult(IStepRunResult stepRunResult);
    void addFlowRunResult(IFlowRunResult flowRunResult);
}
