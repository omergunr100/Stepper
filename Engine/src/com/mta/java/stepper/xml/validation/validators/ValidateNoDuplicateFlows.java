package com.mta.java.stepper.xml.validation.validators;

import com.mta.java.stepper.xml.flow.STFlow;
import com.mta.java.stepper.xml.flow.STFlows;

import java.util.ArrayList;
import java.util.List;

public final class ValidateNoDuplicateFlows implements IFileValidator {
    private STFlows flows;
    private List<String> errors;
    private ValidateNoDuplicateFlows(STFlows flows){
        this.flows = flows;
        this.errors = new ArrayList<>();
    }
    public static IFileValidator getInstance(STFlows flows) {
        return new ValidateNoDuplicateFlows(flows);
    }

    @Override
    public Boolean validate() {
        List<STFlow> flowList = flows.getSTFlow();
        List<String> flowNames = new ArrayList<>();
        for(STFlow flow : flowList){
            if(flowNames.contains(flow.getName().toLowerCase()))
                errors.add("Multiple flows with the same name: " + flow.getName());
            flowNames.add(flow.getName().toLowerCase());
        }

        if(!errors.isEmpty())
            return true;
        return false;
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }
}
