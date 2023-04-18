package com.mta.java.stepper.step.definition.implementation;

import com.mta.java.stepper.data.DDRegistry;
import com.mta.java.stepper.io.api.DataNecessity;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.io.implementation.DataIO;
import com.mta.java.stepper.step.definition.api.AbstractStepDefinition;
import com.mta.java.stepper.step.definition.api.StepResult;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumperStep extends AbstractStepDefinition {
    public FileDumperStep() {
        super("File Dumper", true);
        addInput(new DataIO("CONTENT", "Content", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("FILE_NAME", "Target file path", DataNecessity.MANDATORY, DDRegistry.STRING));
        addOutput(new DataIO("RESULT", "File Creation Result", DDRegistry.STRING));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        // Get dataIOs
        IDataIO contentIO = getInputs().get(0);
        IDataIO fileNameIO = getInputs().get(1);
        IDataIO resultIO = getOutputs().get(0);

        // Get inputs
        String content = (String) context.getInput(contentIO, contentIO.getDataDefinition().getType());
        String fileName = (String) context.getInput(fileNameIO, fileNameIO.getDataDefinition().getType());

        // Create file and dump content
        File file = new File(fileName);
        try {
            if(file.createNewFile()){
                if(content.isEmpty()){
                    context.setSummary("No content to write");
                    context.log("No content to write");
                    context.setOutput(resultIO, "SUCCESS");
                    return StepResult.WARNING;
                }
                else{
                    try{
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(content);
                        fileWriter.close();
                        context.setOutput(resultIO, "SUCCESS");
                        return StepResult.SUCCESS;
                    } catch (IOException e) {
                        context.setSummary("Failed in writing content to file");
                        context.log("Failed in writing content to file: " + e.getMessage());
                        context.setOutput(resultIO, "Failed in writing content to file");
                        return StepResult.FAILURE;
                    }
                }
            }
            else{
                context.setSummary("File already exists");
                context.log("File already exists");
                context.setOutput(resultIO, "File already exists");
                return StepResult.FAILURE;
            }
        } catch (IOException e) {
            context.setSummary("Failed in creation of new file");
            context.log("Failed in creation of new file: " + e.getMessage());
            context.setOutput(resultIO, "Failed in creation of new file");
            return StepResult.FAILURE;
        }
    }
}
