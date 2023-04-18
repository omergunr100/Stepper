package com.main.stepper.xml.validation;

import com.main.stepper.xml.flow.STFlow;
import com.main.stepper.xml.flow.STFlows;
import com.main.stepper.xml.stepper.STStepper;
import com.main.stepper.xml.validation.validators.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Validator {
    private List<String> errors;
    private String path;
    private STStepper stepper;
    private Validator(String path){
        this.errors = new ArrayList<>();
        this.path = path;
    }
    public static Validator getInstance(String path){return new Validator(path);}
    public Boolean validate(){
        File file = new File(path);
        if(!file.getName().endsWith(".xml")){
            errors.add("File at " + path + " is not an xml file!");
            return true;
        }
        if(!file.exists()){
            errors.add("File was not found at " + path);
            return true;
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            stepper = (STStepper) unmarshaller.unmarshal(file);

            // check for duplicate flow names
            IFileValidator fileValidator = ValidateNoDuplicateFlows.getInstance(getFlows());
            if(fileValidator.validate()){
                errors.addAll(fileValidator.getErrors());
            }
            for(STFlow flow : getFlows().getSTFlow()){
                // check for step names that don't exist
                IValidator stepNameValidator = ValidateIllegalStepNames.getInstance(flow);
                if(stepNameValidator.validate())
                    errors.addAll(stepNameValidator.getErrors());

                // check for duplicate step names
                IValidator duplicateStepValidator = ValidateNoDuplicateSteps.getInstance(flow);
                if(duplicateStepValidator.validate())
                    errors.addAll(duplicateStepValidator.getErrors());

                // check for duplicate step aliases
                IValidator duplicateStepAliasValidator = ValidateNoDuplicateStepAlias.getInstance(flow);
                if(duplicateStepAliasValidator.validate())
                    errors.addAll(duplicateStepAliasValidator.getErrors());

                // check for duplicate flow outputs
                IValidator duplicateFlowOutputValidator = ValidateNoDuplicateFlowOutputs.getInstance(flow);
                if(duplicateFlowOutputValidator.validate())
                    errors.addAll(duplicateFlowOutputValidator.getErrors());

                // check for mandatory inputs which are not user friendly
                // check for step/data names that don't exist in the flow
                // check for ordering of steps (can't have the output of a step be the input of a step that comes before it)
                // check for linking of different data types
                // check for aliasing of step/data names that don't exist in the flow
                // check that all flow outputs are linked to some step output
                // check that there aren't multiple mandatory inputs with different data types and same name
            }

        } catch (JAXBException e) {
            errors.add("JAXB error, not supposed to ever happen: " + e.getMessage());
            return true;
        }
        if(!errors.isEmpty())
            return true;
        return false;
    }

    public List<String> getErrors(){
        return errors;
    }
    public STFlows getFlows(){
        return stepper.getSTFlows();
    }
}
