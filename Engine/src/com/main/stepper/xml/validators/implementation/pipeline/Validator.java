package com.main.stepper.xml.validators.implementation.pipeline;

import com.main.stepper.xml.generated.ObjectFactory;
import com.main.stepper.xml.generated.STFlow;
import com.main.stepper.xml.generated.STStepper;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.file.ValidateFile;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateFlowNames;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoIllegalStepsInFlow;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Validator implements IValidator {
    private String path;
    private STStepper stepper;

    public Validator(String path){
        this.path = path;
    }

    public List<String> validate(){
        List<String> errors = new ArrayList<>();

        // Check for errors in the file
        IValidator fileValidator = new ValidateFile(path);
        errors.addAll(fileValidator.validate());
        if(!errors.isEmpty())
            return errors;

        // Get file from validator
        File file = (File) fileValidator.getAdditional().get();

        // Unmarshal file
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            stepper = (STStepper) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            errors.add("JAXB error, not supposed to ever happen: " + e.getMessage());
            return errors;
        }

        // Check for duplicate flow names
        IValidator flowNameValidator = new ValidateNoDuplicateFlowNames(stepper.getSTFlows());
        errors.addAll(flowNameValidator.validate());
        if (!errors.isEmpty())
            return errors;

        // Get flows from validator
        List<STFlow> flows = stepper.getSTFlows().getSTFlow();

        // Check for steps in flows that are not registered
        flows.stream()
                .forEach(
                        flow -> {
                            IValidator illegalStepValidator = new ValidateNoIllegalStepsInFlow(flow);
                            errors.addAll(illegalStepValidator.validate());
                        }
                );
        if (!errors.isEmpty())
            return errors;

        // Check for references to steps that aren't defined in flow







    // check for mandatory inputs which are not user friendly
    // check for step/data names that don't exist in the flow
    // check for ordering of steps (can't have the output of a step be the input of a step that comes before it)
    // check for linking of different data types
    // check for aliasing of step/data names that don't exist in the flow
    // check that all flow outputs are linked to some step output
    // check that there aren't multiple mandatory inputs with different data types and same name

        return errors;
    }

    @Override
    public Optional<STStepper> getAdditional() {
        return Optional.of(stepper);
    }
}
