package com.engine.step.api;

import com.engine.data.api.IDataDefinition;

import java.util.List;

public interface IStepExecutionContext {
    void Log(String message);
    <T> T getInput(String name, IDataDefinition type);
    void setSummary(String summary);
    String getSummary();
}
