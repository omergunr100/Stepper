package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.list.datatype.FileList;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.File;
import java.util.ArrayList;
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

        FileList files = new FileList();
        Arrays.stream(folder.listFiles()).filter(File::isFile).forEach(files::add);
        List<File> remove = new ArrayList<>();
        if(filter != null && !filter.isEmpty())
            files.stream().filter(f->!f.getName().endsWith(filter)).forEach(remove::add);
        files.removeAll(remove);

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
