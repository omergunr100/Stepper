package com.main.stepper.step.execution.api;

import com.main.stepper.io.api.IDataIO;

public interface IStepExecutionContext {
    String getUniqueRunId();
    void log(String message);
    <T> T getInput(IDataIO name, Class<T> type);
    void setOutput(IDataIO name, Object value);
}
