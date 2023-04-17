package com.mta.java.stepper.step.execution.api;

import com.mta.java.stepper.data.api.IDataDefinition;
import com.mta.java.stepper.io.api.IDataIO;

public interface IStepExecutionContext {
    void Log(String message);
    <T> T getInput(IDataIO name, Class<T> type);
    void setOutput(IDataIO name, Object value);
    void setSummary(String summary);
    String getSummary();
}
