package com.main.stepper.step.execution.api;

import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.implementation.data.Log;

import java.util.List;
import java.util.UUID;

public interface IStepExecutionContext {
    UUID getUniqueRunId();
    void log(String message);
    List<Log> getLogs();
    <T> T getInput(IDataIO name, Class<T> type);
    void setOutput(IDataIO name, Object value);
    IDataIO getAliasedDataIO(IDataIO dataIO);
}
