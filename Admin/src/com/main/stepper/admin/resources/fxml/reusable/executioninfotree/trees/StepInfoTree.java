package com.main.stepper.admin.resources.fxml.reusable.executioninfotree.trees;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.dataview.customtreecell.CustomTreeItem;
import com.main.stepper.admin.resources.dataview.list.ListViewController;
import com.main.stepper.admin.resources.dataview.relation.RelationViewController;
import com.main.stepper.data.implementation.list.datatype.GenericList;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import com.main.stepper.shared.structures.step.StepExecutionContextDTO;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class StepInfoTree {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static TreeItem<CustomTreeItem> make(@NotNull StepRunResultDTO stepRunResult) {
        TreeItem<CustomTreeItem> root = new TreeItem<>(new CustomTreeItem()); // hidden root node - 0
        TreeItem<CustomTreeItem> name = new TreeItem<>(new CustomTreeItem("Step Name: " + stepRunResult.name())); // step name - 1
        TreeItem<CustomTreeItem> id = new TreeItem<>(new CustomTreeItem("Step ID: " + stepRunResult.runId())); // step id - 1
        TreeItem<CustomTreeItem> startTime = new TreeItem<>(new CustomTreeItem("Start Time: " + formatter.format(stepRunResult.startTime()))); // step start time - 1
        TreeItem<CustomTreeItem> runTime = new TreeItem<>(new CustomTreeItem("Run Time: " + stepRunResult.duration().toMillis() + " ms")); // step current run time - 1
        TreeItem<CustomTreeItem> result = new TreeItem<>(new CustomTreeItem("Result: " + stepRunResult.result())); // step result - 1
        TreeItem<CustomTreeItem> summary = new TreeItem<>(new CustomTreeItem("Summary: " + stepRunResult.summary())); // step summary - 1
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
        TreeItem<CustomTreeItem> inputsNode = new TreeItem<>(new CustomTreeItem("Inputs:")); // inputs - 1
        if (!inputs.isEmpty()) {
            inputs.forEach(input -> {
                DataIODTO aliasedDataIO = context.getAliasedDataIO(input);
                TreeItem<CustomTreeItem> inputName = new TreeItem<>(new CustomTreeItem("Name: " + aliasedDataIO.name())); // input - 2
                TreeItem<CustomTreeItem> inputType = new TreeItem<>(new CustomTreeItem("Type: " + input.type().getName())); // type - 3
                TreeItem<CustomTreeItem> inputNecessity = new TreeItem<>(new CustomTreeItem("Necessity: " + input.necessity())); // necessity - 3

                Object blob = context.getInput(input, input.type().getType());
                TreeItem<CustomTreeItem> inputValue = null; // value - 3
                if (blob == null) {
                    inputValue = new TreeItem<>(new CustomTreeItem("Value: Not created due to failure in flow"));
                }
                else if (Relation.class.isAssignableFrom(input.type().getType())) {
                    Runnable runnable = () -> {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(RelationViewController.class.getResource("RelationView.fxml"));
                        try {
                            Parent relationView = fxmlLoader.load();
                            RelationViewController relationViewController = fxmlLoader.getController();
                            relationViewController.updateTable((Relation) blob);
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setTitle("Relation view");
                            Scene scene = new Scene(relationView);
                            Bindings.bindContent(scene.getStylesheets(), PropertiesManager.primaryStage.get().getScene().getStylesheets());
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException ignored) {
                        }
                    };
                    inputValue = new TreeItem<>(new CustomTreeItem("Value:", "Show", runnable));
                }
                else if (GenericList.class.isAssignableFrom(input.type().getType())) {
                    Runnable runnable = () -> {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(ListViewController.class.getResource("ListView.fxml"));
                        try {
                            Parent listView = fxmlLoader.load();
                            ListViewController listViewController = fxmlLoader.getController();
                            listViewController.loadList((List) blob);
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setTitle("List view");
                            Scene scene = new Scene(listView);
                            Bindings.bindContent(scene.getStylesheets(), PropertiesManager.primaryStage.get().getScene().getStylesheets());
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException ignored) {
                        }
                    };
                    inputValue = new TreeItem<>(new CustomTreeItem("Value:", "Show", runnable));
                }
                else {
                    inputValue = new TreeItem<>(new CustomTreeItem("Value: " + blob));
                }

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
        TreeItem<CustomTreeItem> outputsNode = new TreeItem<>(new CustomTreeItem("Outputs:")); // outputs - 1
        if (!outputs.isEmpty()) {
            outputs.forEach(output -> {
                DataIODTO aliasedDataIO = context.getAliasedDataIO(output);
                TreeItem<CustomTreeItem> outputName = new TreeItem<>(new CustomTreeItem("Name: " + aliasedDataIO.name())); // output - 2
                TreeItem<CustomTreeItem> outputType = new TreeItem<>(new CustomTreeItem("Type: " + output.type().getName())); // type - 3

                Object blob = context.getInput(output, output.type().getType());
                TreeItem<CustomTreeItem> outputValue = null; // value - 3
                if (blob == null) {
                    outputValue = new TreeItem<>(new CustomTreeItem("Value: Not created due to failure in flow"));
                }
                else if (Relation.class.isAssignableFrom(output.type().getType())) {
                    Runnable runnable = () -> {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(RelationViewController.class.getResource("RelationView.fxml"));
                        try {
                            Parent relationView = fxmlLoader.load();
                            RelationViewController relationViewController = fxmlLoader.getController();
                            relationViewController.updateTable((Relation) blob);
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setTitle("Relation view");
                            Scene scene = new Scene(relationView);
                            Bindings.bindContent(scene.getStylesheets(), PropertiesManager.primaryStage.get().getScene().getStylesheets());
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException ignored) {
                        }
                    };
                    outputValue = new TreeItem<>(new CustomTreeItem("Value:", "Show", runnable));
                }
                else if (GenericList.class.isAssignableFrom(output.type().getType())) {
                    Runnable runnable = () -> {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(ListViewController.class.getResource("ListView.fxml"));
                        try {
                            Parent listView = fxmlLoader.load();
                            ListViewController listViewController = fxmlLoader.getController();
                            listViewController.loadList((List) blob);
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setTitle("List view");
                            Scene scene = new Scene(listView);
                            Bindings.bindContent(scene.getStylesheets(), PropertiesManager.primaryStage.get().getScene().getStylesheets());
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException ignored) {
                        }
                    };
                    outputValue = new TreeItem<>(new CustomTreeItem("Value:", "Show", runnable));
                }
                else {
                    outputValue = new TreeItem<>(new CustomTreeItem("Value: " + blob));
                }

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
        TreeItem<CustomTreeItem> logsNode = new TreeItem<>(new CustomTreeItem("Logs:")); // logs - 1
        if (!logs.isEmpty()) {
            logs.forEach(log -> {
                TreeItem<CustomTreeItem> logNode = new TreeItem<>(new CustomTreeItem(log.toString()));
                // add log to logsNode
                logsNode.getChildren().add(logNode);
            });
        }
        root.getChildren().add(logsNode);

        return root;
    }
}
