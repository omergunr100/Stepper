package com.main.stepper.application.resources.fxml.tabs.executionshistory;

import com.main.stepper.application.resources.fxml.reusable.flowrundetails.FlowRunDetailsController;
import javafx.fxml.FXML;

public class ExecutionHistoryScreenController {
    @FXML private FlowRunDetailsController detailsTableController;

    public ExecutionHistoryScreenController() {
    }

    @FXML public void initialize() {
    }

    public void reset() {
        detailsTableController.reset();
    }
}
