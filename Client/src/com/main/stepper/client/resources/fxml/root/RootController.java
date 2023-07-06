package com.main.stepper.client.resources.fxml.root;

import com.main.stepper.client.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.client.resources.fxml.tabs.executionshistory.tab.ExecutionHistoryScreenController;
import com.main.stepper.client.resources.fxml.tabs.statistics.tab.StatisticsScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RootController {
    private Stage primaryStage;
    @FXML TabPane tabs;
    @FXML Tab flowsDefinitionTab;
    @FXML Tab flowsExecutionTab;
    @FXML Tab executionsHistoryTab;
    @FXML Tab statisticsTab;
    @FXML LoadCSSController loadCSSController;
    @FXML ExecutionHistoryScreenController flowExecutionHistoryController;
    @FXML StatisticsScreenController statisticsScreenController;

    public RootController() {
    }

    @FXML public void initialize(){
        // set root controller
        loadCSSController.setRootController(this);
        // select initial window
        tabs.getSelectionModel().select(flowsDefinitionTab);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
