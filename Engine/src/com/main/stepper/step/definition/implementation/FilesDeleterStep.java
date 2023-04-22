package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.file.FileData;
import com.main.stepper.data.implementation.list.datatype.FileList;
import com.main.stepper.data.implementation.list.datatype.StringList;
import com.main.stepper.data.implementation.mapping.implementation.IntToIntPair;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;

public class FilesDeleterStep extends AbstractStepDefinition {
    public FilesDeleterStep() {
        super("Files Deleter", false);
        addInput(new DataIO("FILES_LIST", "Files to delete", DataNecessity.MANDATORY, DDRegistry.FILE_LIST));
        addOutput(new DataIO("DELETED_LIST", "Files failed to be deleted", DDRegistry.STRING_LIST));
        addOutput(new DataIO("DELETION_STATS", "Deletion summary results", DDRegistry.INT_TO_INT_MAPPING));
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Temporal startTime = LocalTime.now();
        // Read inputs
        IDataIO filesListIO = getInputs().get(0);
        FileList filesList = (FileList) context.getInput(filesListIO, DDRegistry.FILE_LIST.getType());

        // Get outputs for later
        IDataIO deletedListIO = getOutputs().get(0);
        IDataIO deletionStatsIO = getOutputs().get(1);

        context.log("About to start delete " + filesList.size() + " files");

        StringList failedToDelete = new StringList();
        context.setOutput(deletedListIO, failedToDelete);

        // Handle empty deletion list case
        if(filesList.size() == 0){
            IntToIntPair deletionStats = new IntToIntPair(0, 0);
            context.setOutput(deletionStatsIO, deletionStats);
            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "No files to delete");
        }

        // Try to delete each file
        for(FileData file : filesList){
            if(!file.delete()){
                context.log("Failed to delete file: " + file.getAbsolutePath());
                failedToDelete.add(file.getName());
            }
        }

        IntToIntPair deletionStats = new IntToIntPair(filesList.size() - failedToDelete.size(), failedToDelete.size());
        context.setOutput(deletionStatsIO, deletionStats);

        // Handle special cases
        if(failedToDelete.size() == filesList.size()){
            context.log("Failed to delete all files");

            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, duration, "Failed to delete all files");
        }
        else if(failedToDelete.size() > 0){
            context.log("Failed to delete " + failedToDelete.size() + " files");

            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.WARNING, duration, "Failed to delete " + failedToDelete.size() + " files");
        }

        Duration duration = Duration.between(startTime, LocalTime.now());
        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "Success");
    }
}
