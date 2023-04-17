package com.mta.java.stepper.xml.validation.validators;

import com.mta.java.stepper.xml.flow.STFlow;
import com.mta.java.stepper.xml.steps.STStepInFlow;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ValidateNoDuplicateSteps implements IValidator{
    private STFlow flow;
    private List<String> errors;
    private ValidateNoDuplicateSteps(STFlow flow){
        this.flow = flow;
        this.errors = new ArrayList<>();
    }
    public static IValidator getInstance(STFlow flow) {
        return new ValidateNoDuplicateSteps(flow);
    }

    @Override
    public Boolean validate() {
        List<String> stepNames = new ArrayList<>();
        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow()){
            System.out.println(flow.getName() + " :: " + step.getName() + " :: " + step.getAlias());
            // TODO: not clear if alias check is enough if present or the step type matters as well
            String concat = (step.getName() +"|"+ step.getAlias()).toLowerCase();
            if(stepNames.contains(concat)){
                String s = "";
                try {
                    JAXBContext context = JAXBContext.newInstance(STStepInFlow.class);
                    Marshaller marshaller = context.createMarshaller();
                    StringWriter writer = new StringWriter();
                    marshaller.marshal(step, writer);
                    errors.add("Multiple steps with the same name: " + writer.toString());
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }
            }
            else
                stepNames.add(concat);
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
