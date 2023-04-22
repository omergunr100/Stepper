package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.list.datatype.FileList;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Arrays;

public class CollectFilesInFolderStep extends AbstractStepDefinition {
    public CollectFilesInFolderStep() {
        super("Collect Files In Folder", true);
        addInput(new DataIO("FOLDER_NAME", "Folder name to scan", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("FILTER", "Filter only these files", DataNecessity.OPTIONAL, DDRegistry.STRING));
        addOutput(new DataIO("FILES_LIST", "Files list", DDRegistry.FILE_LIST));
        addOutput(new DataIO("TOTAL_FOUND", "Total files found", DDRegistry.NUMBER));
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Temporal startTime = LocalTime.now();
        // Read inputs
        IDataIO folderNameIO = getInputs().get(0);
        IDataIO filterIO = getInputs().get(1);
        String folderName = (String) context.getInput(folderNameIO, DDRegistry.STRING.getType());
        String filter = (String) context.getInput(filterIO, DDRegistry.STRING.getType());

        FileList files = new FileList();
        IDataIO filesListIO = getOutputs().get(0);
        context.setOutput(filesListIO, files);
        // Validate inputs
        File folder = new File(folderName);
        if(!folder.exists()){
            context.log("Folder does not exist at: " + folderName);

            IDataIO totalFoundIO = getOutputs().get(1);
            context.setOutput(totalFoundIO, files.size());
            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, duration, "Folder does not exist at: " + folderName);
        }
        if(!folder.isDirectory()){
            context.log("Path is not a directory: " + folderName);

            IDataIO totalFoundIO = getOutputs().get(1);
            context.setOutput(totalFoundIO, files.size());
            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, duration, "Path is not a directory: " + folderName);
        }
        context.log("Reading folder " + folderName + " content with filter " + filter);

        Arrays.stream(folder.listFiles()).filter(File::isFile).forEach(files::add);
        FileList remove = new FileList();
        if(filter != null && !filter.isEmpty())
            files.stream().filter(f->!f.getName().endsWith(filter)).forEach(remove::add);
        files.removeAll(remove);

        if(files.isEmpty()){
            context.log("No files found in folder matching the filter");

            IDataIO totalFoundIO = getOutputs().get(1);
            context.setOutput(totalFoundIO, files.size());
            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.WARNING, duration, "No files found in folder matching the filter");
        }

        context.log("Found " + files.size() + " files in folder matching the filter");

        IDataIO totalFoundIO = getOutputs().get(1);

        context.setOutput(totalFoundIO, files.size());

        Duration duration = Duration.between(startTime, LocalTime.now());
        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "Success");
    }
}
