package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public class FileDumperStep extends AbstractStepDefinition {
    public FileDumperStep() {
        super("File Dumper", true);
        addInput(new DataIO("CONTENT", "Content", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("FILE_NAME", "Target file path", DataNecessity.MANDATORY, DDRegistry.STRING));
        addOutput(new DataIO("RESULT", "File Creation Result", DDRegistry.STRING));
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Temporal startTime = LocalTime.now();
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
                    context.log("No content to write");
                    context.setOutput(resultIO, "SUCCESS");

                    Duration duration = Duration.between(startTime, LocalTime.now());
                    return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.WARNING, duration, "No content to write");
                }
                else{
                    try{
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(content);
                        fileWriter.close();
                        context.setOutput(resultIO, "SUCCESS");

                        Duration duration = Duration.between(startTime, LocalTime.now());
                        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "Success");
                    } catch (IOException e) {
                        context.log("Failed in writing content to file: " + e.getMessage());
                        context.setOutput(resultIO, "Failed in writing content to file");

                        Duration duration = Duration.between(startTime, LocalTime.now());
                        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, duration, "Failed in writing content to file");
                    }
                }
            }
            else{
                context.log("File already exists");
                context.setOutput(resultIO, "File already exists");

                Duration duration = Duration.between(startTime, LocalTime.now());
                return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, duration, "File already exists");
            }
        } catch (IOException e) {
            context.log("Failed in creation of new file: " + e.getMessage());
            context.setOutput(resultIO, "Failed in creation of new file");

            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, duration, "Failed in creation of new file");
        }
    }
}
