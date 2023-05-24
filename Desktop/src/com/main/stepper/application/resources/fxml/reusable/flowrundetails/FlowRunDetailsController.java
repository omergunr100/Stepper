package com.main.stepper.application.resources.fxml.reusable.flowrundetails;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FlowRunDetailsController {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
    private IFlowRunResult currentFlow;
    @FXML TableView<IFlowRunResult> table;

    public FlowRunDetailsController() {
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

        // todo: set on row selection listener
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setCurrentFlow(newValue);
            if (newValue != null){
                // todo: update selection view
            }
        });

        // set resize policy
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setCurrentFlow(IFlowRunResult result){
        this.currentFlow = result;
    }

    public IFlowRunResult getCurrentFlow(){
        return this.currentFlow;
    }
    public void updateTable(List<IFlowRunResult> results){
        table.getItems().clear();
        table.getItems().addAll(FXCollections.observableArrayList(results));
    }
}
