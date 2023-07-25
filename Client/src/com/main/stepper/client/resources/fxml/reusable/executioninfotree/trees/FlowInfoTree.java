package com.main.stepper.client.resources.fxml.reusable.executioninfotree.trees;

import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FlowInfoTree {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static TreeItem<String> make(@NotNull FlowRunResultDTO flowRunResult) {
        // initialize tree components
        TreeItem<String> root = new TreeItem<>(); // hidden root node - 0
        TreeItem<String> name = new TreeItem<>("Flow Name: " + flowRunResult.name()); // flow name - 1
        TreeItem<String> id = new TreeItem<>("Flow ID: " + flowRunResult.runId()); // flow id - 1
        TreeItem<String> startTime = new TreeItem<>("Start Time: " + formatter.format(flowRunResult.startTime())); // flow start time - 1
        TreeItem<String> userInputs = new TreeItem<>("User Inputs:"); // user inputs - 1
        // initialize user input nodes
        synchronized (flowRunResult) {
            flowRunResult.userInputs().forEach((key, value) -> {
                TreeItem<String> input = new TreeItem<>("Name: " + key.name()); // input - 2
                TreeItem<String> type = new TreeItem<>("Type: " + key.type()); // type - 3
                TreeItem<String> necessity = new TreeItem<>("Necessity: " + key.necessity()); // necessity - 3
                TreeItem<String> valueNode = new TreeItem<>("Value: " + value.toString()); // value - 3
                input.getChildren().addAll(
                        type,
                        necessity,
                        valueNode
                );
                userInputs.getChildren().add(input);
            });
        }
        TreeItem<String> result = new TreeItem<>("Result: " + flowRunResult.result()); // flow result - 1
        TreeItem<String> runTime = new TreeItem<>("Run Time: " + flowRunResult.duration().toMillis() + " ms"); // flow current run time - 1
        // add all unconditional t1 nodes to root
        root.getChildren().addAll(
                name,
                id,
                startTime,
                userInputs,
                result,
                runTime
        );
        if (!flowRunResult.result().equals(FlowResult.RUNNING)) {
            TreeItem<String> outputs = new TreeItem<>("Formal Outputs:"); // outputs - 1
            // initialize output nodes
            synchronized (flowRunResult) {
                flowRunResult.flowOutputs().forEach((key, value) -> {
                    TreeItem<String> output = new TreeItem<>("Name: " + key.name()); // output - 2
                    TreeItem<String> type = new TreeItem<>("Type: " + key.type()); // type - 3
                    // todo: add special handling for certain data types
                    TreeItem<String> valueNode = new TreeItem<>("Value: " + value.toString()); // value - 3
                    output.getChildren().addAll(
                            type,
                            valueNode
                    );
                    outputs.getChildren().add(output);
                });
            }
            // add outputs to root
            root.getChildren().add(outputs);
        }

        return root;
    }
}
