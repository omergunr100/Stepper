package com.main.stepper.application.resources.fxml.tabs.flowsdefinition;

import com.main.stepper.application.resources.fxml.reusable.flowdetails.FlowTreeViewController;
import com.main.stepper.application.resources.fxml.root.RootController;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FlowsDefinitionController {
    private RootController rootController;
    private IFlowDefinition currentFlow;
    @FXML private TableView<IFlowDefinition> flowsTableView;
    @FXML private FlowTreeViewController selectedFlowTreeController;
    @FXML private Button executeFlowButton;

    public FlowsDefinitionController() {
    }

    @FXML public void initialize(){
        // Have no flow selected initially
        this.currentFlow = null;

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

        flowsTableView.getColumns().addAll(nameColumn, descriptionColumn, numberOfStepsColumn, numberOfFreeInputsColumn, numberOfContinuationsColumn);

        flowsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setCurrentFlow(newValue);
            this.executeFlowButton.setDisable(newValue == null);
        });

        flowsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    public RootController getRootController() {
        return rootController;
    }

    public void setCurrentFlow(IFlowDefinition currentFlow) {
        this.currentFlow = currentFlow;
        if (currentFlow != null)
            selectedFlowTreeController.setCurrentFlow(currentFlow.information());
        else
            selectedFlowTreeController.setCurrentFlow(null);
    }

    public void updateFlows() {
        flowsTableView.setItems(FXCollections.observableArrayList(rootController.getEngine().getFlows()));
        selectedFlowTreeController.setCurrentFlow(null);
    }

    public void executeFlow() {
        if (currentFlow != null) {
            rootController.executeFlow(currentFlow);
        }
    }
}
