package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.util.List;

public class PropertiesExporterStep extends AbstractStepDefinition{
    public PropertiesExporterStep() {
        super("Properties Exporter", true);
        addInput(new DataIO("SOURCE", "Source data", DataNecessity.MANDATORY, DDRegistry.RELATION));
        addOutput(new DataIO("RESULT", "Properties export result", DDRegistry.STRING));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        // Get dataIOs
        IDataIO sourceIO = getInputs().get(0);
        IDataIO resultIO = getOutputs().get(0);

        // Get variable
        Relation source = (Relation) context.getInput(sourceIO, DDRegistry.RELATION.getType());

        // Export to properties
        context.log("About to process " + source.getRowCount() + " lines of data");

        // Empty case
        if(source.getRowCount() == 0){
            context.setOutput(resultIO, "");
            context.setSummary("There is no data to export");
            context.log("There is no data to export");
            return StepResult.WARNING;
        }

        // Else
        StringBuilder builder = new StringBuilder();

        List<String> columns = source.getColumns();
        for(String title : columns) {
            List<String> rows = source.getColumnByRowsOrder(title);
            for (Integer i = 1; i <= rows.size(); i++) {
                builder
                        .append("row-")
                        .append(i.toString())
                        .append(".")
                        .append(title)
                        .append("=")
                        .append(rows.get(i - 1))
                        .append("\r\n");
            }
        }

        // Set output
        context.setOutput(resultIO, builder.toString());

        return StepResult.SUCCESS;
    }
}
