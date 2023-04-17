package com.mta.java.stepper.step.definition.implementation;

import com.mta.java.stepper.data.DDRegistry;
import com.mta.java.stepper.io.api.DataNecessity;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.io.implementation.DataIO;
import com.mta.java.stepper.step.definition.api.AbstractStepDefinition;
import com.mta.java.stepper.step.definition.api.StepResult;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectFilesInFolderStep extends AbstractStepDefinition {
    public CollectFilesInFolderStep() {
        super("Collect Files In Folder", true);
        addInput(new DataIO("FOLDER_NAME", "Folder name to scan", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("FILTER", "Filter only these files", DataNecessity.OPTIONAL, DDRegistry.STRING));
        addOutput(new DataIO("FILES_LIST", "Files list", DDRegistry.FILE_LIST));
        addOutput(new DataIO("TOTAL_FOUND", "Total files found", DDRegistry.NUMBER));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        // Read inputs
        IDataIO folderNameIO = getInputs().get(0);
        IDataIO filterIO = getInputs().get(1);
        String folderName = (String) context.getInput(folderNameIO, DDRegistry.STRING.getType());
        String filter = (String) context.getInput(filterIO, DDRegistry.STRING.getType());

        // Validate inputs
        File folder = new File(folderName);
        if(!folder.exists()){
            context.log("Folder does not exist at: " + folderName);
            context.setSummary("Folder does not exist at: " + folderName);
            return StepResult.FAILURE;
        }
        if(!folder.isDirectory()){
            context.log("Path is not a directory: " + folderName);
            context.setSummary("Path is not a directory: " + folderName);
            return StepResult.FAILURE;
        }
        context.log("Reading folder " + folderName + " content with filter " + filter);

        List<File> files = Arrays.stream(folder.listFiles()).filter(f->f.isFile()).collect(Collectors.toList());
        if(filter != null && !filter.isEmpty())
            files = files.stream().filter(f->f.getName().endsWith(filter)).collect(Collectors.toList());

        if(files.isEmpty()){
            context.log("No files found in folder matching the filter");
            context.setSummary("No files found in folder matching the filter");
            return StepResult.WARNING;
        }

        context.log("Found " + files.size() + " files in folder matching the filter");

        IDataIO filesListIO = getOutputs().get(0);
        IDataIO totalFoundIO = getOutputs().get(1);

        context.setOutput(filesListIO, files);
        context.setOutput(totalFoundIO, files.size());

        return StepResult.SUCCESS;
    }
}
