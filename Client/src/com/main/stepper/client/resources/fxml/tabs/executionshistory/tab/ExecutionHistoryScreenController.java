package com.main.stepper.client.resources.fxml.tabs.executionshistory.tab;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.fxml.reusable.executionelements.FlowExecutionElementsController;
import com.main.stepper.client.resources.fxml.reusable.executioninfotree.ExecutionInfoTreeController;
import com.main.stepper.client.resources.fxml.reusable.executionstepsbox.ExecutionStepsBoxController;
import com.main.stepper.client.resources.fxml.reusable.selector.SelectorController;
import com.main.stepper.client.resources.fxml.reusable.stepdetails.StepDetailsController;
import com.main.stepper.client.resources.fxml.tabs.executionshistory.flowrundetails.FlowRunDetailsController;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.Optional;

public class ExecutionHistoryScreenController {
    @FXML private FlowRunDetailsController detailsTableController;
    @FXML private SelectorController userSelectorController;
    @FXML private Button rerunFlowButton;

    @FXML private ExecutionStepsBoxController flowStepsBoxController;
    @FXML private ExecutionInfoTreeController infoTreeController;

    public ExecutionHistoryScreenController() {
    }

    @FXML public void initialize() {
        flowStepsBoxController.setBindings(PropertiesManager.executionHistorySelectedFlow, PropertiesManager.executionHistorySelectedStep, PropertiesManager.historyIsFlowSelected);
        infoTreeController.setBindings(PropertiesManager.executionHistorySelectedFlow, PropertiesManager.executionHistorySelectedStep, PropertiesManager.historyIsFlowSelected);
        userSelectorController.setProperty(PropertiesManager.executionHistorySelectedUser);

        // initialize rerun button
        rerunFlowButton.setDisable(true);
        // set listeners for rerun button disable property
        PropertiesManager.executionHistorySelectedFlow.addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                rerunFlowButton.setDisable(true);
            else {
                rerunFlowButton.setDisable(!checkIfFlowIsRerunnable(newValue.flowInfo()));
            }
        });

        PropertiesManager.flowInformationList.addListener((ListChangeListener<? super FlowInfoDTO>) c -> {
            if (PropertiesManager.executionHistorySelectedFlow.isNotNull().get()) {
                rerunFlowButton.setDisable(!checkIfFlowIsRerunnable(PropertiesManager.executionHistorySelectedFlow.get().flowInfo()));
            }
        });
    }

    private Boolean checkIfFlowIsRerunnable(FlowInfoDTO flowInfoDTO) {
        if (flowInfoDTO == null)
            return false;

        synchronized (PropertiesManager.flowInformationList) {
            Optional<FlowInfoDTO> match = PropertiesManager.flowInformationList.stream().filter(flow -> flow.name().equals(flowInfoDTO.name())).findFirst();
            if (match.isPresent())
                return true;
        }
        return false;
    }

    @FXML private void onRerunButtonPress() {
        PropertiesManager.reloadFlow.set(PropertiesManager.executionHistorySelectedFlow.get());
    }
}
