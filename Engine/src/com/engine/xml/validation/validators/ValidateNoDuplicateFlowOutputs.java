package com.engine.xml.validation.validators;

import com.engine.xml.flow.STFlow;

import java.util.ArrayList;
import java.util.List;

public class ValidateNoDuplicateFlowOutputs implements IValidator{
    private STFlow flow;
    private List<String> errors;
    private ValidateNoDuplicateFlowOutputs(STFlow flow){
        this.flow = flow;
        this.errors = new ArrayList<>();
    }
    public static IValidator getInstance(STFlow flow) {
        return new ValidateNoDuplicateFlowOutputs(flow);
    }
    @Override
    public Boolean validate() {
        List<String> flowOutputs = new ArrayList<>();

        for(String output : flow.getSTFlowOutput().split(",")){
            if(flowOutputs.contains(output))
                errors.add("Multiple flow outputs with the same name: " + output);
            else
                flowOutputs.add(output);
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
