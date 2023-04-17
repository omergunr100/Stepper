package com.mta.java.stepper.step.definition;

import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.step.definition.api.IStepDefinition;
import com.mta.java.stepper.step.definition.implementation.*;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;
import com.mta.java.stepper.step.definition.api.StepResult;

import java.util.List;

public enum StepRegistry implements IStepDefinition {
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    FILES_DELETER(new FilesDeleterStep()),
    FILES_RENAMER(new FilesRenamerStep()),
    FILES_CONTENT_EXTRACTOR(new FilesContentExtractorStep())
    ;

    private final IStepDefinition step;

    StepRegistry(IStepDefinition step) {
        this.step = step;
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
    public StepResult execute(IStepExecutionContext context) {
        return step.execute(context);
    }
}
