package com.main.stepper.engine.definition.api;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.io.api.IDataIO;

import java.util.List;
import java.util.UUID;

public interface IEngine {
    static IEngine getInstance(){return null;}

    /**
     * @param path - path to the xml file
     * @return - list of errors, if there were no errors, the list will be empty
     */
    List<String> readSystemFromXML(String path) throws XMLException;

    /**
     * @return - list of flow names
     */
    List<String> getFlowNames();

    /**
     * @param name - name of the flow
     * @return - flow info
     */
    IFlowInformation getFlowInfo(String name);

    /**
     * @param flowName - name of the flow
     * @return - list of mandatory inputs that are not connected to any step (and optional)
     */
    ExecutionUserInputs getExecutionUserInputs(String flowName);

    /**
     * @param name - name of the flow
     * @return - run result object
     */
    IFlowRunResult runFlow(String name, ExecutionUserInputs inputs);

    /**
     * @return - list of flow runs
     */
    List<String> getFlowRuns();

    /**
     * @param runId - run UUID
     * @return - flow run result object
     */
    IFlowRunResult getFlowRunInfo(UUID runId);

    /**
     * @return - statistics about step/flow runs
     */
    String getStatistics();

    /**
     * @param flowName - name of the flow
     * @return - list of mandatory inputs that are not connected to any step
     */
    List<IDataIO> getFreeMandatoryInputs(String flowName);

    /**
     * @param flowName - name of the flow
     * @return - list of optional inputs that are not connected to any step
     */
    List<IDataIO> getFreeOptionalInputs(String flowName);
}
