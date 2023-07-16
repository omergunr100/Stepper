package com.main.stepper.client.resources.fxml.root;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.client.resources.fxml.tabs.executionshistory.tab.ExecutionHistoryScreenController;
import com.main.stepper.client.resources.fxml.tabs.flowsdefinition.FlowsDefinitionController;
import com.main.stepper.client.resources.fxml.tabs.flowsexecution.tab.FlowExecutionController;
import com.main.stepper.client.resources.fxml.tabs.statistics.tab.StatisticsScreenController;
import com.main.stepper.shared.structures.roles.Role;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RootController {
    // stage
    private Stage primaryStage;
    // root
    @FXML private GridPane root;
    // header elements
    @FXML private LoadCSSController loadCSSController;
    @FXML private TextField userNameTextField;
    @FXML private CheckBox isManagerCheckBox;
    @FXML private TextField assignedRolesTextField;
    // tabs
    @FXML private TabPane tabs;
    @FXML private Tab flowsDefinitionTab;
    @FXML private Tab flowsExecutionTab;
    @FXML private Tab executionsHistoryTab;
    @FXML private Tab statisticsTab;
    // tab screen controllers
    @FXML private FlowsDefinitionController flowsDefinitionController;
    @FXML private FlowExecutionController flowExecutionController;
    @FXML private ExecutionHistoryScreenController flowExecutionHistoryController;
    @FXML private StatisticsScreenController statisticsScreenController;

    public RootController() {
    }

    @FXML public void initialize(){
        // bind to properties
        PropertiesManager.health.addListener((observable, oldValue, newValue) -> {
            root.setDisable(!newValue);
        });
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
