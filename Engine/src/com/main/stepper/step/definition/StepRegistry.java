package com.main.stepper.step.definition;

import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.IStepDefinition;
import com.main.stepper.step.definition.implementation.*;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.util.List;

public enum StepRegistry implements IStepDefinition {
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    FILES_DELETER(new FilesDeleterStep()),
    FILES_RENAMER(new FilesRenamerStep()),
    FILES_CONTENT_EXTRACTOR(new FilesContentExtractorStep()),
    CSV_EXPORTER(new CSVExporterStep()),
    PROPERTIES_EXPORTER(new PropertiesExporterStep()),
    FILE_DUMPER(new FileDumperStep()),
    ZIPPER(new ZipperStep()),
    COMMAND_LINE(new CommandLineStep()),
    HTTP_CALL(new HttpCallStep()),
    JASONATOR(new JasonatorStep()),
    ;

    private final IStepDefinition step;

    StepRegistry(IStepDefinition step) {
        this.step = step;
    }

    @Override
    public Class<? extends AbstractStepDefinition> getStepClass() {
        return step.getStepClass();
    }

    @Override
    public String getName() {
        return step.getName();
    }

    @Override
    public Boolean isReadOnly() {
        return step.isReadOnly();
    }

    @Override
    public List<IDataIO> getInputs() {
        return step.getInputs();
    }

    @Override
    public List<IDataIO> getOutputs() {
        return step.getOutputs();
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        return step.execute(context);
    }

    @Override
    public StepDefinitionDTO toDTO() {
        return step.toDTO();
    }
}
