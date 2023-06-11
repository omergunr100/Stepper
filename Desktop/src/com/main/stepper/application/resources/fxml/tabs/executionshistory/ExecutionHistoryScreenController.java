package com.main.stepper.application.resources.fxml.tabs.executionshistory;

import com.main.stepper.application.resources.fxml.reusable.flowrundetails.FlowRunDetailsController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements.FlowExecutionElementsController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.stepdetails.StepDetailsController;
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
        stepDetailsController.setExecutionHistoryScreenController(this);
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
