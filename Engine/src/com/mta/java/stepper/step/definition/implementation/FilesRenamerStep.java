package com.mta.java.stepper.step.definition.implementation;

import com.mta.java.stepper.data.DDRegistry;
import com.mta.java.stepper.data.implementation.list.datatype.FileList;
import com.mta.java.stepper.data.implementation.relation.Relation;
import com.mta.java.stepper.io.api.DataNecessity;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.io.implementation.DataIO;
import com.mta.java.stepper.step.definition.api.AbstractStepDefinition;
import com.mta.java.stepper.step.definition.api.StepResult;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FilesRenamerStep extends AbstractStepDefinition {
    public FilesRenamerStep() {
        super("Files Renamer", false);
        addInput(new DataIO("FILES_TO_RENAME", "Files to rename", DataNecessity.MANDATORY, DDRegistry.FILE_LIST));
        addInput(new DataIO("PREFIX", "Add this prefix", DataNecessity.OPTIONAL, DDRegistry.STRING));
        addInput(new DataIO("SUFFIX", "Append this suffix", DataNecessity.OPTIONAL, DDRegistry.STRING));
        addOutput(new DataIO("RENAME_RESULT", "Rename operation summary", DDRegistry.RELATION));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        // Get DataIO
        IDataIO filesToRenameIO = getInputs().get(0);
        IDataIO prefixIO = getInputs().get(1);
        IDataIO suffixIO = getInputs().get(2);
        IDataIO renameResultIO = getOutputs().get(0);

        // Initialize output table
        List<String> columns = Arrays.asList("Index", "Original name", "New name");
        Relation renameResult = new Relation(columns);

        // Get inputs and put outputs
        FileList filesToRename = (FileList) context.getInput(filesToRenameIO, DDRegistry.FILE_LIST.getType());
        Optional<String> prefix = Optional.ofNullable((String) context.getInput(prefixIO, DDRegistry.STRING.getType()));
        Optional<String> suffix = Optional.ofNullable((String) context.getInput(suffixIO, DDRegistry.STRING.getType()));
        context.setOutput(renameResultIO, renameResult);

        // Renaming files
        context.log("About to start rename " + filesToRename.size() + " files.\n" + "Adding prefix: " + prefix.orElse("") + "; " + "Adding suffix: " + suffix.orElse(""));

        // Empty case
        if(filesToRename.size() == 0){
            context.log("No files to rename.");
            context.setSummary("No files to rename.");
            return StepResult.SUCCESS;
        }
        // Else
        String failed = "";
        Integer numbering = 1;
        for(File file : filesToRename){
            String newName = prefix.orElse("");
            String originalName = file.getName();
            String name = originalName.substring(0, originalName.lastIndexOf('.'));
            String extension = originalName.substring(originalName.lastIndexOf('.'));
            newName += name + suffix.orElse("") + extension;
            if(file.renameTo(new File(file.getParent() + "\\" + newName))){
                renameResult.addRow(Arrays.asList(numbering.toString(), originalName, newName));
                numbering++;
            }
            else{
                renameResult.addRow(Arrays.asList(numbering.toString(), originalName, originalName));
                context.log("Problem renaming file " + file.getAbsolutePath());
                failed += file.getAbsolutePath() + "\n";
            }
        }

        // Failure to change at least 1
        if(!failed.equals("")){
            context.setSummary("Failed to rename the following files:\n" + failed);
            return StepResult.WARNING;
        }

        return StepResult.SUCCESS;
    }
}
