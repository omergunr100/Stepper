package com.main.stepper.step.execution.api;

import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.implementation.data.Log;

import java.util.List;

public interface IStepExecutionContext {
    String getUniqueRunId();
    void log(String message);
    List<Log> getLogs();
    <T> T getInput(IDataIO name, Class<T> type);
    void setOutput(IDataIO name, Object value);
    IDataIO getAliasedDataIO(IDataIO dataIO);
}
