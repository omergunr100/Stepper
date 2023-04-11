package com.engine;

import com.engine.data.io.Input;
import com.engine.flows.Flow;
import com.engine.xml.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Engine implements IEngine{
    static IEngine getInstance(){
        return new Engine();
    }
    public static void main(String[] args) {
        Engine engine = new Engine();
    }
    private List<Flow> flows;
    public Engine(){
        // TODO: Read file info from user
        Validator validator = Validator.getInstance("C:\\test\\xml\\ex1.xml");
        if(validator.validate()){
            for(String error : validator.getErrors())
                System.out.println(error);
        }

        this.flows = new ArrayList<>();
    }
    public void readSystemFromFile(String path){

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
    public UUID runFlow(String name) {
        return null;
    }

    @Override
    public List<String> getFlowRuns() {
        return null;
    }

    @Override
    public String getFlowRunInfo(UUID runId) {
        return null;
    }

    @Override
    public String getStatistics() {
        return null;
    }

    @Override
    public List<Input> getFreeMandatoryInputs(String flowName) {
        return null;
    }

    @Override
    public List<Input> getFreeOptionalInputs(String flowName) {
        return null;
    }
}
