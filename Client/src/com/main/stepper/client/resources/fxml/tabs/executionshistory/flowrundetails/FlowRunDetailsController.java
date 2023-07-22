package com.main.stepper.client.resources.fxml.tabs.executionshistory.flowrundetails;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.main.stepper.client.resources.data.PropertiesManager.executionHistorySelectedUser;
import static com.main.stepper.client.resources.data.PropertiesManager.flowRunResults;

public class FlowRunDetailsController {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
    @FXML private TableView<FlowRunResultDTO> table;

    private final TableColumn<FlowRunResultDTO, String> userColumn = new TableColumn<>("User");

    private ObservableList<FlowRunResultDTO> currentBinding;

    public FlowRunDetailsController() {
    }

    @FXML public void initialize(){
        // initialize currentBinding
        currentBinding = flowRunResults;

        // add columns to table
        TableColumn<FlowRunResultDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));
        nameColumn.setPrefWidth(150);

        TableColumn<FlowRunResultDTO, String> timeColumn = new TableColumn<>("Run-Time");
        timeColumn.setCellValueFactory(param -> new SimpleStringProperty(dateTimeFormatter.format(param.getValue().startTime())));
        nameColumn.setPrefWidth(250);

        TableColumn<FlowRunResultDTO, String> resultColumn = new TableColumn<>("Result");
        resultColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().result().toString()));
        nameColumn.setPrefWidth(150);

        userColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().user()));
        userColumn.setPrefWidth(150);

        table.getColumns().addAll(nameColumn, timeColumn, resultColumn);

        // add listener on manager status to disable/enable user column
        PropertiesManager.isManager.addListener((observable, oldValue, newValue) -> {
            if(!oldValue && newValue) {
                table.getColumns().add(userColumn);
            } else if (oldValue) {
                table.getColumns().remove(userColumn);
            }
        });

        // bind selected flow to the table selection
        PropertiesManager.executionHistorySelectedFlow.bind(table.getSelectionModel().selectedItemProperty());

        // set resize policy
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // add initial binding on flowRunResults
        Bindings.bindContent(table.itemsProperty().get(), currentBinding);
        // add listener on selector change
        executionHistorySelectedUser.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Bindings.unbindContent(table.itemsProperty().get(), currentBinding);
                currentBinding = flowRunResults.filtered(result -> (executionHistorySelectedUser.get().equals("") ? true : result.user().equals(executionHistorySelectedUser.get())));
                Bindings.bindContent(table.itemsProperty().get(), currentBinding);
            }
        });
    }
}
