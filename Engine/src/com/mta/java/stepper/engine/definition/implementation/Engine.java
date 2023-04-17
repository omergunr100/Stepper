package com.mta.java.stepper.engine.definition.implementation;

import com.mta.java.stepper.engine.definition.api.IEngine;
import com.mta.java.stepper.engine.executor.api.IFlowRunResult;
import com.mta.java.stepper.flow.definition.api.IFlowDefinition;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.logger.api.ILogger;
import com.mta.java.stepper.logger.implementation.MapLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Engine implements IEngine {
    static IEngine getInstance(){
        return new Engine();
    }

    private final ILogger logger;
    private List<IFlowDefinition> flows;

    public Engine(){
        // TODO: Read file info from user
//        Validator validator = Validator.getInstance("C:\\test\\xml\\ex1.xml");
//        if(validator.validate()){
//            for(String error : validator.getErrors())
//                System.out.println(error);
//        }

        this.logger = new MapLogger();
        this.flows = new ArrayList<>();
    }

    @Override
    public List<String> readSystemFromXML(String path) {
        return null;
    }

    @Override
    public List<String> getFlowNames() {
        return null;
    }

    @Override
    public String getFlowInfo(String name) {
        return null;
    }

    @Override
    public IFlowRunResult runFlow(String name) {
        return null;
    }

    @Override
    public List<String> getFlowRuns() {
        return null;
    }

    @Override
    public IFlowRunResult getFlowRunInfo(UUID runId) {
        return null;
    }

    @Override
    public String getStatistics() {
        return null;
    }

    @Override
    public List<IDataIO> getFreeMandatoryInputs(String flowName) {
        return null;
    }

    @Override
    public List<IDataIO> getFreeOptionalInputs(String flowName) {
        return null;
    }
}
