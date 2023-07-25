package com.main.stepper.admin.resources.fxml.reusable.executioninfotree.trees;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.dataview.customtreecell.CustomTreeItem;
import com.main.stepper.admin.resources.dataview.list.ListViewController;
import com.main.stepper.admin.resources.dataview.relation.RelationViewController;
import com.main.stepper.data.implementation.list.datatype.GenericList;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
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

public final class FlowInfoTree {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static TreeItem<CustomTreeItem> make(@NotNull FlowRunResultDTO flowRunResult) {
        // initialize tree components
        TreeItem<CustomTreeItem> root = new TreeItem<>(new CustomTreeItem()); // hidden root node - 0
        TreeItem<CustomTreeItem> name = new TreeItem<>(new CustomTreeItem("Flow Name: " + flowRunResult.name())); // flow name - 1
        TreeItem<CustomTreeItem> id = new TreeItem<>(new CustomTreeItem("Flow ID: " + flowRunResult.runId())); // flow id - 1
        TreeItem<CustomTreeItem> startTime = new TreeItem<>(new CustomTreeItem("Start Time: " + formatter.format(flowRunResult.startTime()))); // flow start time - 1
        TreeItem<CustomTreeItem> userInputs = new TreeItem<>(new CustomTreeItem("User Inputs:")); // user inputs - 1
        // initialize user input nodes
        synchronized (flowRunResult) {
            flowRunResult.userInputs().forEach((key, value) -> {
                TreeItem<CustomTreeItem> input = new TreeItem<>(new CustomTreeItem("Name: " + key.name())); // input - 2
                TreeItem<CustomTreeItem> type = new TreeItem<>(new CustomTreeItem("Type: " + key.type())); // type - 3
                TreeItem<CustomTreeItem> necessity = new TreeItem<>(new CustomTreeItem("Necessity: " + key.necessity())); // necessity - 3
                TreeItem<CustomTreeItem> valueNode = new TreeItem<>(new CustomTreeItem("Value: " + value.toString())); // value - 3
                input.getChildren().addAll(
                        type,
                        necessity,
                        valueNode
                );
                userInputs.getChildren().add(input);
            });
        }
        TreeItem<CustomTreeItem> result = new TreeItem<>(new CustomTreeItem("Result: " + flowRunResult.result())); // flow result - 1
        TreeItem<CustomTreeItem> runTime = new TreeItem<>(new CustomTreeItem("Run Time: " + flowRunResult.duration().toMillis() + " ms")); // flow current run time - 1
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
            TreeItem<CustomTreeItem> outputs = new TreeItem<>(new CustomTreeItem("Formal Outputs:")); // outputs - 1
            // initialize output nodes
            synchronized (flowRunResult) {
                flowRunResult.flowOutputs().forEach((key, value) -> {
                    TreeItem<CustomTreeItem> output = new TreeItem<>(new CustomTreeItem("Name: " + key.name())); // output - 2
                    TreeItem<CustomTreeItem> type = new TreeItem<>(new CustomTreeItem("Type: " + key.type())); // type - 3

                    TreeItem<CustomTreeItem> valueNode = null; // value - 3
                    if (value == null) {
                        valueNode = new TreeItem<>(new CustomTreeItem("Value: Not created due to failure in flow"));
                    }
                    else if (Relation.class.isAssignableFrom(key.type().getType())) {
                        Runnable runnable = () -> {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(RelationViewController.class.getResource("RelationView.fxml"));
                            try {
                                Parent relationView = fxmlLoader.load();
                                RelationViewController relationViewController = fxmlLoader.getController();
                                relationViewController.updateTable((Relation) value);
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
                        valueNode = new TreeItem<>(new CustomTreeItem("Value:", "Show", runnable));
                    }
                    else if (GenericList.class.isAssignableFrom(key.type().getType())) {
                        Runnable runnable = () -> {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(ListViewController.class.getResource("ListView.fxml"));
                            try {
                                Parent listView = fxmlLoader.load();
                                ListViewController listViewController = fxmlLoader.getController();
                                listViewController.loadList((List) value);
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
                        valueNode = new TreeItem<>(new CustomTreeItem("Value:", "Show", runnable));
                    }
                    else {
                        valueNode = new TreeItem<>(new CustomTreeItem("Value: " + value));
                    }
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
