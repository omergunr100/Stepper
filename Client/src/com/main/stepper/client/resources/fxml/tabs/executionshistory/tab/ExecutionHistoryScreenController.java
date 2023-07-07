package com.main.stepper.client.resources.fxml.tabs.executionshistory.tab;

import com.main.stepper.client.resources.fxml.reusable.executionelements.FlowExecutionElementsController;
import com.main.stepper.client.resources.fxml.tabs.executionshistory.flowrundetails.FlowRunDetailsController;
import com.main.stepper.client.resources.fxml.reusable.stepdetails.StepDetailsController;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import javafx.fxml.FXML;

public class ExecutionHistoryScreenController {
    @FXML private FlowRunDetailsController detailsTableController;
    @FXML private FlowExecutionElementsController stepDetailsController;
    @FXML private StepDetailsController flowExecutionStepsController;

    public ExecutionHistoryScreenController() {
    }

    @FXML public void initialize() {
        detailsTableController.setParent(this);
        stepDetailsController.setParent(this);
    }

    public void reset() {
        detailsTableController.reset();
        stepDetailsController.reset();
        flowExecutionStepsController.reset();
    }

    public void selectFlowRunDetails(IFlowRunResult result) {
        stepDetailsController.forceLoadAllElementsFrom(result);
    }

    public void selectStepRunDetails(IStepRunResult result) {
        flowExecutionStepsController.setStep(result);
    }
}
