package com.main.stepper.client.resources.fxml.reusable.executioninfotree.trees;

import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import com.main.stepper.shared.structures.step.StepExecutionContextDTO;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class StepInfoTree {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static TreeItem<String> make(@NotNull StepRunResultDTO stepRunResult) {
        TreeItem<String> root = new TreeItem<>(); // hidden root node - 0
        TreeItem<String> name = new TreeItem<>("Step Name: " + stepRunResult.name()); // step name - 1
        TreeItem<String> id = new TreeItem<>("Step ID: " + stepRunResult.runId()); // step id - 1
        TreeItem<String> startTime = new TreeItem<>("Start Time: " + formatter.format(stepRunResult.startTime())); // step start time - 1
        TreeItem<String> runTime = new TreeItem<>("Run Time: " + stepRunResult.duration().toMillis() + " ms"); // step current run time - 1
        TreeItem<String> result = new TreeItem<>("Result: " + stepRunResult.result()); // step result - 1
        TreeItem<String> summary = new TreeItem<>("Summary: " + stepRunResult.summary()); // step summary - 1
        // add all unconditional t1 nodes to root
        root.getChildren().addAll(
                name,
                id,
                startTime,
                result,
                runTime,
                summary
        );

        StepDefinitionDTO step = stepRunResult.step();
        List<DataIODTO> inputs = step.inputs();
        List<DataIODTO> outputs = step.outputs();
        StepExecutionContextDTO context = stepRunResult.context();
        List<Log> logs = context.getLogs();
        // check for inputs
        TreeItem<String> inputsNode = new TreeItem<>("Inputs:"); // inputs - 1
        if (!inputs.isEmpty()) {
            inputs.forEach(input -> {
                DataIODTO aliasedDataIO = context.getAliasedDataIO(input);
                TreeItem<String> inputName = new TreeItem<>("Name: " + aliasedDataIO.name()); // input - 2
                TreeItem<String> inputType = new TreeItem<>("Type: " + input.type().getName()); // type - 3
                TreeItem<String> inputNecessity = new TreeItem<>("Necessity: " + input.necessity()); // necessity - 3

                Object blob = context.getInput(input, input.type().getType());
                // todo: add special handling for certain data types
                TreeItem<String> inputValue = new TreeItem<>("Value: " + blob.toString()); // value - 3

                // add all t3 nodes to inputNode
                inputName.getChildren().addAll(
                        inputType,
                        inputNecessity,
                        inputValue
                );
                // add input to inputsNode
                inputsNode.getChildren().add(inputName);
            });
            // add inputNode to root
        }
        root.getChildren().add(inputsNode);

        // check for outputs
        TreeItem<String> outputsNode = new TreeItem<>("Outputs:"); // outputs - 1
        if (!outputs.isEmpty()) {
            outputs.forEach(output -> {
                DataIODTO aliasedDataIO = context.getAliasedDataIO(output);
                TreeItem<String> outputName = new TreeItem<>("Name: " + aliasedDataIO.name()); // output - 2
                TreeItem<String> outputType = new TreeItem<>("Type: " + output.type().getName()); // type - 3

                Object blob = context.getInput(output, output.type().getType());
                // todo: add special handling for certain data types
                TreeItem<String> outputValue = new TreeItem<>("Value: " + (blob == null ? "" : blob.toString())); // value - 3

                // add all t3 nodes to outputNode
                outputName.getChildren().addAll(
                        outputType,
                        outputValue
                );
                // add output to outputsNode
                outputsNode.getChildren().add(outputName);
            });
            // add outputNode to root
        }
        root.getChildren().add(outputsNode);

        // check for logs
        TreeItem<String> logsNode = new TreeItem<>("Logs:"); // logs - 1
        if (!logs.isEmpty()) {
            logs.forEach(log -> {
                TreeItem<String> logNode = new TreeItem<>(log.toString());
                // add log to logsNode
                logsNode.getChildren().add(logNode);
            });
        }
        root.getChildren().add(logsNode);

        return root;
    }
}
