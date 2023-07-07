package com.main.stepper.client.resources.fxml.tabs.flowsexecution.continuations;

import com.main.stepper.application.resources.fxml.tabs.flowsexecution.tab.FlowExecutionController;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class FlowContinuationsController {
    private FlowExecutionController parent;
    @FXML TableView<IFlowDefinition> continuationsTable;

    public FlowContinuationsController() {
    }

    @FXML public void initialize(){
        // Initialize flow table
        TableColumn<IFlowDefinition, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));
        nameColumn.setPrefWidth(100);


        TableColumn<IFlowDefinition, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().description()));
        descriptionColumn.setPrefWidth(200);

        TableColumn<IFlowDefinition, String> numberOfStepsColumn = new TableColumn<>("Number of steps");
        numberOfStepsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().steps().size())));
        numberOfStepsColumn.setPrefWidth(150);

        TableColumn<IFlowDefinition, String> numberOfFreeInputsColumn = new TableColumn<>("Number of free inputs");
        numberOfFreeInputsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().userRequiredInputs().size() + param.getValue().userOptionalInputs().size())));
        numberOfFreeInputsColumn.setPrefWidth(150);

        TableColumn<IFlowDefinition, String> numberOfContinuationsColumn = new TableColumn<>("Number of continuations");
        numberOfContinuationsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().continuations().size())));
        numberOfContinuationsColumn.setPrefWidth(150);

        continuationsTable.getColumns().addAll(nameColumn, descriptionColumn, numberOfStepsColumn, numberOfFreeInputsColumn, numberOfContinuationsColumn);

        continuationsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                parent.setCurrentFlow(newValue, FlowExecutor.lastFinishedFlowResult.getValue());
                continuationsTable.getSelectionModel().clearSelection();
            }
            continuationsTable.setVisible(false);
        });

        continuationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setParent(FlowExecutionController parent) {
        this.parent = parent;
    }

    public void setContinuations(List<IFlowDefinition> continuations) {
        reset();
        if (!continuations.isEmpty()) {
            continuationsTable.getItems().addAll(continuations);
            continuationsTable.setVisible(true);
        }
    }

    public void reset() {
        continuationsTable.getItems().clear();
    }
}
