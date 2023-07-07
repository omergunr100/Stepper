package com.main.stepper.client.resources.fxml.tabs.flowsexecution.continuations;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.fxml.tabs.flowsexecution.tab.FlowExecutionController;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.stream.Collectors;

public class FlowContinuationsController {
    @FXML TableView<FlowInfoDTO> continuationsTable;

    public FlowContinuationsController() {
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

        continuationsTable.getColumns().addAll(nameColumn, descriptionColumn, numberOfStepsColumn, numberOfFreeInputsColumn, numberOfContinuationsColumn);

        continuationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // listen for change in selection and update selected flow
        continuationsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            PropertiesManager.currentFlowExecutionUserInputs.set(null);
            PropertiesManager.continuation.set(PropertiesManager.flowRunResults.get(0));
            PropertiesManager.executionSelectedFlow.set(newValue);
        });

        // listen for when to show continuations table
        // when (currentFlow is the same as the last flow in run history) and (final run history has status that is not running) and (isFlowExecutionRunning is false)
        BooleanBinding currentFlowIsLastFlowRun = Bindings.createBooleanBinding(() -> PropertiesManager.flowRunResults.isEmpty() || PropertiesManager.executionSelectedFlow.isNull().getValue() ? false : PropertiesManager.flowRunResults.get(0).name().equals(PropertiesManager.executionSelectedFlow.getName()), PropertiesManager.flowRunResults, PropertiesManager.executionSelectedFlow);
        BooleanBinding lastFlowFinished = Bindings.createBooleanBinding(() -> PropertiesManager.flowRunResults.isEmpty() ? false : !PropertiesManager.flowRunResults.get(0).result().equals(FlowResult.RUNNING), PropertiesManager.flowRunResults);
        PropertiesManager.isFlowExecutionRunning.not()
                .and(currentFlowIsLastFlowRun)
                .and(lastFlowFinished).addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        setContinuations(PropertiesManager.executionSelectedFlow.get().continuations());
                    } else {
                        reset();
                    }
                });
    }

    public void setContinuations(List<String> continuations) {
        reset();
        if (!continuations.isEmpty()) {
            List<FlowInfoDTO> collect;
            synchronized (PropertiesManager.flowInformationList) {
                collect = PropertiesManager.flowInformationList.stream().filter(info -> continuations.contains(info.name())).collect(Collectors.toList());
            }
            if (!collect.isEmpty()) {
                // set continuations
                continuationsTable.getItems().addAll(collect);
                continuationsTable.setVisible(true);
            }
        }
    }

    public void reset() {
        continuationsTable.getItems().clear();
        continuationsTable.setVisible(false);
    }
}
