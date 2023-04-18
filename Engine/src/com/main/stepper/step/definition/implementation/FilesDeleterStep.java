package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;
import com.main.stepper.data.implementation.list.datatype.FileList;
import javafx.util.Pair;

import java.io.File;

public class FilesDeleterStep extends AbstractStepDefinition {
    public FilesDeleterStep() {
        super("Files Deleter", false);
        addInput(new DataIO("FILES_LIST", "Files to delete", DataNecessity.MANDATORY, DDRegistry.FILE_LIST));
        addOutput(new DataIO("DELETED_LIST", "Files failed to be deleted", DDRegistry.STRING_LIST));
        addOutput(new DataIO("DELETION_STATS", "Deletion summary results", DDRegistry.MAPPING));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        // Read inputs
        IDataIO filesListIO = getInputs().get(0);
        FileList filesList = (FileList) context.getInput(filesListIO, DDRegistry.FILE_LIST.getType());

        // Get outputs for later
        IDataIO deletedListIO = getOutputs().get(0);
        IDataIO deletionStatsIO = getOutputs().get(1);

        context.log("About to start delete " + filesList.size() + " files");

        FileList failedToDelete = new FileList();
        context.setOutput(deletedListIO, failedToDelete);

        // Handle empty deletion list case
        if(filesList.size() == 0){
            Pair<Integer, Integer> deletionStats = new Pair<>(0, 0);
            context.setOutput(deletionStatsIO, deletionStats);
            context.setSummary("No files to delete");
            return StepResult.SUCCESS;
        }

        // Try to delete each file
        for(File file : filesList){
            if(!file.delete()){
                context.log("Failed to delete file: " + file.getAbsolutePath());
                failedToDelete.add(file);
            }
        }

        Pair<Integer, Integer> deletionStats = new Pair<>(filesList.size() - failedToDelete.size(), failedToDelete.size());
        context.setOutput(deletionStatsIO, deletionStats);

        // Handle special cases
        if(failedToDelete.size() == filesList.size()){
            context.setSummary("Failed to delete all files");
            context.log("Failed to delete all files");
            return StepResult.FAILURE;
        }
        else if(failedToDelete.size() > 0){
            context.setSummary("Failed to delete " + failedToDelete.size() + " files");
            context.log("Failed to delete " + failedToDelete.size() + " files");
            return StepResult.WARNING;
        }

        return StepResult.SUCCESS;
    }
}
