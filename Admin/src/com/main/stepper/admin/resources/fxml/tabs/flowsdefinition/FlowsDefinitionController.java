package com.main.stepper.admin.resources.fxml.tabs.flowsdefinition;

import com.main.stepper.admin.resources.fxml.reusable.flowdetails.FlowTreeViewController;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FlowsDefinitionController {
    @FXML private TableView<FlowInfoDTO> flowsTableView;
    @FXML private FlowTreeViewController selectedFlowTreeController;
    @FXML private Button executeFlowButton;

    public FlowsDefinitionController() {
    }

    @FXML public void initialize(){
        // Initialize flow table
        TableColumn<FlowInfoDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));
        nameColumn.setPrefWidth(100);


        TableColumn<FlowInfoDTO, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().description()));
        descriptionColumn.setPrefWidth(200);

        TableColumn<FlowInfoDTO, String> numberOfStepsColumn = new TableColumn<>("Number of steps");
        numberOfStepsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().steps().size())));
        numberOfStepsColumn.setPrefWidth(150);

        TableColumn<FlowInfoDTO, String> numberOfFreeInputsColumn = new TableColumn<>("Number of free inputs");
        numberOfFreeInputsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().openUserInputs().size())));
        numberOfFreeInputsColumn.setPrefWidth(150);

        TableColumn<FlowInfoDTO, String> numberOfContinuationsColumn = new TableColumn<>("Number of continuations");
        numberOfContinuationsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().continuations().size())));
        numberOfContinuationsColumn.setPrefWidth(150);

        flowsTableView.getColumns().addAll(nameColumn, descriptionColumn, numberOfStepsColumn, numberOfFreeInputsColumn, numberOfContinuationsColumn);

        flowsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // todo: either use or delete this (can be used to select a flow or repurpose the table to show the users/roles available)
    }
}
