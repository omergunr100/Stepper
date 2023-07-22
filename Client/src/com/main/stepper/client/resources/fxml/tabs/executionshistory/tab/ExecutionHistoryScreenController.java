package com.main.stepper.client.resources.fxml.tabs.executionshistory.tab;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.fxml.reusable.executionelements.FlowExecutionElementsController;
import com.main.stepper.client.resources.fxml.reusable.selector.SelectorController;
import com.main.stepper.client.resources.fxml.reusable.stepdetails.StepDetailsController;
import com.main.stepper.client.resources.fxml.tabs.executionshistory.flowrundetails.FlowRunDetailsController;
import javafx.fxml.FXML;

public class ExecutionHistoryScreenController {
    @FXML private FlowRunDetailsController detailsTableController;
    @FXML private FlowExecutionElementsController flowExecutionElementsController;
    @FXML private StepDetailsController stepDetailsController;
    @FXML private SelectorController userSelectorController;

    public ExecutionHistoryScreenController() {
    }

    @FXML public void initialize() {
        flowExecutionElementsController.setBindings(PropertiesManager.executionHistorySelectedFlow, PropertiesManager.executionHistorySelectedStep);
        stepDetailsController.setBinding(PropertiesManager.executionHistorySelectedStep);
        userSelectorController.setProperty(PropertiesManager.executionHistorySelectedUser);
    }
}
