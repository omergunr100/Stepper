package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.list.datatype.FileList;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FilesContentExtractorStep extends AbstractStepDefinition {
    public FilesContentExtractorStep() {
        super("Files Content Extractor", true);
        addInput(new DataIO("FILES_LIST", "Files to extract", DataNecessity.MANDATORY, DDRegistry.FILE_LIST));
        addInput(new DataIO("LINE", "Line number to extract", DataNecessity.MANDATORY, DDRegistry.NUMBER));
        addOutput(new DataIO("DATA", "Data extraction", DDRegistry.RELATION));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        // Get the DataIOs
        IDataIO filesListIO = getInputs().get(0);
        IDataIO lineNumberIO = getInputs().get(1);
        IDataIO dataResultIO = getOutputs().get(0);

        // Initialize output table
        List<String> columns = Arrays.asList("Index", "Original name", "Extracted data");
        Relation data = new Relation(columns);

        // Get inputs and put outputs
        FileList filesToExtract = (FileList) context.getInput(filesListIO, DDRegistry.FILE_LIST.getType());
        Integer lineNumber = (Integer) context.getInput(lineNumberIO, DDRegistry.NUMBER.getType());
        context.setOutput(dataResultIO, data);

        // Extracting data
        // Empty case
        if(filesToExtract.size() == 0){
            context.log("No files to extract");
            context.setSummary("No files to extract");
            return StepResult.SUCCESS;
        }
        // Else
        for(Integer i = 1; i <= filesToExtract.size(); i++){
            File file = filesToExtract.get(i - 1);
            context.log("About to start work on file: " + file.getName());
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                Optional<String> line = reader.lines().skip(lineNumber - 1).limit(1).findFirst();
                data.addRow(Arrays.asList(i.toString(), file.getName(), line.orElse("No such line")));
            } catch (FileNotFoundException e) {
                context.log("Problem extracting line number " + lineNumber + " from file: " + file.getName());
                data.addRow(Arrays.asList(i.toString(), file.getName(), "File not found"));
            }
        }

        return StepResult.SUCCESS;
    }
}
