package com.main.stepper.engine.definition.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.exceptions.engine.NotAFileException;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.statistics.StatManager;

import java.io.IOException;
import java.util.List;

public class ServerEngine implements IEngine {

    public ServerEngine() {
    }

    @Override
    public List<String> readSystemFromXML(String path) throws XMLException {
        return null;
    }

    @Override
    public List<String> getFlowNames() {
        return null;
    }

    @Override
    public List<IFlowDefinition> getFlows() {
        return null;
    }

    @Override
    public IFlowInformation getFlowInfo(String name) {
        return null;
    }

    @Override
    public ExecutionUserInputs getExecutionUserInputs(String flowName) {
        return null;
    }

    @Override
    public IFlowRunResult runFlow(String name, ExecutionUserInputs inputs) {
        return null;
    }

    @Override
    public List<IFlowRunResult> getFlowRuns() {
        return null;
    }

    @Override
    public IFlowRunResult getFlowRunInfo(String runId) {
        return null;
    }

    @Override
    public StatManager getStatistics() {
        return null;
    }

    @Override
    public List<Log> getLogs(String uuid) {
        return null;
    }

    @Override
    public void writeSystemToFile(String path) throws NotAFileException, IOException {

    }

    @Override
    public Boolean readSystemFromFile(String path) throws NotAFileException, IOException {
        return null;
    }
}
