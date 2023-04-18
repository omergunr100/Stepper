package com.main.stepper.step.execution.api;

import com.main.stepper.io.api.IDataIO;

public interface IStepExecutionContext {
    void log(String message);
    <T> T getInput(IDataIO name, Class<T> type);
    void setOutput(IDataIO name, Object value);
    void setSummary(String summary);
    String getSummary();
}
