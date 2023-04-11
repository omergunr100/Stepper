package com.engine.xml.validation.validators;

import com.engine.xml.flow.STFlow;
import com.engine.xml.steps.STStepInFlow;

import java.util.ArrayList;
import java.util.List;

public class ValidateNoDuplicateStepAlias implements IValidator{
    private STFlow flow;
    private List<String> errors;
    private ValidateNoDuplicateStepAlias(STFlow flow){
        this.flow = flow;
        this.errors = null;
    }
    public static IValidator getInstance(STFlow flow) {
        return new ValidateNoDuplicateStepAlias(flow);
    }
    @Override
    public Boolean validate() {
        List<String> stepAliases = new ArrayList<>();

        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow()){
            if(step.getAlias() != null && !step.getAlias().isEmpty()){
                if(stepAliases.contains(step.getAlias())){
                    errors.add("Multiple steps with the same alias: " + step.getAlias());
                }
                else
                    stepAliases.add(step.getAlias());
            }
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
