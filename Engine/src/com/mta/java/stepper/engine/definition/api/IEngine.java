package com.mta.java.stepper.engine.definition.api;

import com.mta.java.stepper.engine.executor.api.IFlowRunResult;
import com.mta.java.stepper.engine.executor.implementation.FlowRunResult;
import com.mta.java.stepper.io.api.IDataIO;

import java.util.List;
import java.util.UUID;

public interface IEngine {
    static IEngine getInstance(){return null;}

    /**
     * @param path - path to the xml file
     * @return - list of errors, if there were no errors, the list will be empty
     */
    List<String> readSystemFromXML(String path);

    /**
     * @return - list of flow names
     */
    List<String> getFlowNames();

    /**
     * @param name - name of the flow
     * @return - flow info
     */
    String getFlowInfo(String name);

    /**
     * @param name - name of the flow
     * @return - run result object
     */
    IFlowRunResult runFlow(String name);

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
