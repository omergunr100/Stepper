package com.main.stepper.application.resources.fxml.reusable.flowrundetails;

import com.main.stepper.application.resources.fxml.tabs.executionshistory.ExecutionHistoryScreenController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements.FlowExecutionElementsController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.stepdetails.StepDetailsController;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FlowRunDetailsController {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
    private IFlowRunResult currentFlow;
    @FXML TableView<IFlowRunResult> table;
    private ExecutionHistoryScreenController parent;

    public FlowRunDetailsController() {
    }

    public void reset() {
        currentFlow = null;
        table.getItems().clear();
    }

    public void setParent(ExecutionHistoryScreenController parent) {
        this.parent = parent;
    }

    @FXML public void initialize(){
        // initial value
        this.currentFlow = null;
        // add columns to table
        TableColumn<IFlowRunResult, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));
        nameColumn.setPrefWidth(150);

        TableColumn<IFlowRunResult, String> timeColumn = new TableColumn<>("Run-Time");
        timeColumn.setCellValueFactory(param -> new SimpleStringProperty(dateTimeFormatter.format(param.getValue().startTime())));
        nameColumn.setPrefWidth(250);

        TableColumn<IFlowRunResult, String> resultColumn = new TableColumn<>("Result");
        resultColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().result().toString()));
        nameColumn.setPrefWidth(150);

        table.getColumns().addAll(nameColumn, timeColumn, resultColumn);

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setCurrentFlow(newValue);
        });

        // set resize policy
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);

        // add listener on finished flows
        FlowExecutor.lastFinishedFlowResult.addListener((observable, oldValue, newValue) -> {
            table.getItems().add(0, newValue);
        });
    }

    private void setCurrentFlow(IFlowRunResult result){
        currentFlow = result;
        if (result != null)
            parent.selectFlowRunDetails(result);
    }

    public IFlowRunResult getCurrentFlow(){
        return this.currentFlow;
    }
}
