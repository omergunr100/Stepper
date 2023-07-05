package com.main.stepper.xml.validators.implementation.pipeline;

import com.main.stepper.xml.generated.ex2.ObjectFactory;
import com.main.stepper.xml.generated.ex2.STFlow;
import com.main.stepper.xml.generated.ex2.STStepper;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.file.ValidateFile;
import com.main.stepper.xml.validators.implementation.flow.ValidateContinuationNames;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateFlowNames;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoIllegalStepsInFlow;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Validator implements IValidator {
    public enum Type {
        FILE,
        STRING
    }
    private String string;
    private Type type;
    private STStepper stepper;

    public Validator(String string, Type type){
        this.string = string;
    }

    public List<String> validate(){
        List<String> errors = new ArrayList<>();

        if (type.equals(Type.FILE)) {
            // Check for errors in the file
            IValidator fileValidator = new ValidateFile(string);
            errors.addAll(fileValidator.validate());
            if (!errors.isEmpty())
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
        } else if (type.equals(Type.STRING)) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                StringReader reader = new StringReader(string);
                stepper = (STStepper) unmarshaller.unmarshal(reader);
            } catch (JAXBException e) {
                errors.add("JAXB error, not supposed to ever happen: " + e.getMessage());
                return errors;
            }
        }

        // Check for duplicate flow names
        IValidator flowNameValidator = new ValidateNoDuplicateFlowNames(stepper.getSTFlows());
        errors.addAll(flowNameValidator.validate());
        if (!errors.isEmpty())
            return errors;

        // Get flows from validator
        List<STFlow> flows = stepper.getSTFlows().getSTFlow();

        // check for continuation names that don't exist
        IValidator continuationNameValidator = new ValidateContinuationNames(flows);
        errors.addAll(continuationNameValidator.validate());

        // Check for steps in flows that are not registered
        flows.stream()
                .forEach(
                        flow -> {
                            IValidator illegalStepValidator = new ValidateNoIllegalStepsInFlow(flow);
                            errors.addAll(illegalStepValidator.validate());
                        }
                );

        return errors;
    }

    @Override
    public Optional<STStepper> getAdditional() {
        return Optional.of(stepper);
    }
}
