package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.relation.Relation;
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

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
        super("CSV Exporter", true);
        addInput(new DataIO("SOURCE", "Source data", DataNecessity.MANDATORY, DDRegistry.RELATION));
        addOutput(new DataIO("RESULT", "CSV export result", DDRegistry.STRING));
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Temporal startTime = LocalTime.now();
        // Get dataIOs
        IDataIO sourceIO = getInputs().get(0);
        IDataIO resultIO = getOutputs().get(0);

        // Get data
        Relation source = (Relation) context.getInput(sourceIO, sourceIO.getDataDefinition().getType());

        // Export to CSV
        StringBuilder builder = new StringBuilder();

        // Generate row line
        source.getColumns().forEach(column -> builder.append(column).append(","));
        builder.replace(builder.length() - 1, builder.length(), "\n");

        context.log("About to process " + source.getRowCount() + " lines of data");

        // Empty case
        if(source.getRowCount() == 0){
            context.setOutput(resultIO, builder.toString());
            context.log("There is no data to export");

            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "There is no data to export");
        }
        // Else
        for(int i = 0; i < source.getRowCount(); i++) {
            source.getRowByColumnsOrder(i).forEach(value -> builder.append(value).append(","));
            builder.replace(builder.length() - 1, builder.length(), "\n");
        }

        // Set output
        context.setOutput(resultIO, builder.toString());

        Duration duration = Duration.between(startTime, LocalTime.now());
        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "Success");
    }
}
