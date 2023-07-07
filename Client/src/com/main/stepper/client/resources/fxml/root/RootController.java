package com.main.stepper.client.resources.fxml.root;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.client.resources.fxml.tabs.executionshistory.tab.ExecutionHistoryScreenController;
import com.main.stepper.client.resources.fxml.tabs.flowsdefinition.FlowsDefinitionController;
import com.main.stepper.client.resources.fxml.tabs.flowsexecution.tab.FlowExecutionController;
import com.main.stepper.client.resources.fxml.tabs.statistics.tab.StatisticsScreenController;
import com.main.stepper.shared.structures.roles.Role;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RootController {
    // stage
    private Stage primaryStage;
    // header elements
    @FXML LoadCSSController loadCSSController;
    @FXML TextField userNameTextField;
    @FXML CheckBox isManagerCheckBox;
    @FXML TextField assignedRolesTextField;
    // tabs
    @FXML TabPane tabs;
    @FXML Tab flowsDefinitionTab;
    @FXML Tab flowsExecutionTab;
    @FXML Tab executionsHistoryTab;
    @FXML Tab statisticsTab;
    // tab screen controllers
    @FXML FlowsDefinitionController flowsDefinitionController;
    @FXML FlowExecutionController flowExecutionController;
    @FXML ExecutionHistoryScreenController flowExecutionHistoryController;
    @FXML StatisticsScreenController statisticsScreenController;

    public RootController() {
    }

    @FXML public void initialize(){
        // bind to properties
        userNameTextField.textProperty().bind(PropertiesManager.userName);
        isManagerCheckBox.selectedProperty().bind(PropertiesManager.isManager);
        PropertiesManager.roles.addListener((ListChangeListener.Change<? extends Role> c) -> {
            StringBuilder sb = new StringBuilder();
            ObservableList<Role> roles = PropertiesManager.roles;
            for (Role role : roles) {
                sb.append(role.name()).append(", ");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }
            assignedRolesTextField.setText(sb.toString());
        });
        // set root controller
        loadCSSController.setRootController(this);
        // select initial window
        tabs.getSelectionModel().select(flowsDefinitionTab);
        // on execution flow selection change (if not null go to tab)
        PropertiesManager.executionSelectedFlow.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tabs.getSelectionModel().select(flowsExecutionTab);
            }
        });
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
