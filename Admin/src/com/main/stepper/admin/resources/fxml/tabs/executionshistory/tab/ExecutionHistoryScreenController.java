package com.main.stepper.admin.resources.fxml.tabs.executionshistory.tab;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.fxml.reusable.executioninfotree.ExecutionInfoTreeController;
import com.main.stepper.admin.resources.fxml.reusable.executionstepsbox.ExecutionStepsBoxController;
import com.main.stepper.admin.resources.fxml.reusable.selector.SelectorController;
import com.main.stepper.admin.resources.fxml.tabs.executionshistory.flowrundetails.FlowRunDetailsController;
import javafx.fxml.FXML;

public class ExecutionHistoryScreenController {
    @FXML private FlowRunDetailsController detailsTableController;
    @FXML private SelectorController userSelectorController;
    @FXML private ExecutionStepsBoxController flowStepsBoxController;
    @FXML private ExecutionInfoTreeController infoTreeController;

    public ExecutionHistoryScreenController() {
    }

    @FXML public void initialize() {
        userSelectorController.setProperty(PropertiesManager.executionHistorySelectedUser);
        flowStepsBoxController.setBindings(PropertiesManager.executionHistorySelectedFlow, PropertiesManager.executionHistorySelectedStep, PropertiesManager.historyIsFlowSelected);
        infoTreeController.setBindings(PropertiesManager.executionHistorySelectedFlow, PropertiesManager.executionHistorySelectedStep, PropertiesManager.historyIsFlowSelected);
        userSelectorController.forceTriggerChange();
    }
}
