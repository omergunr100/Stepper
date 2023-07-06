package com.main.stepper.admin.resources.fxml.root;

import com.main.stepper.admin.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.admin.resources.fxml.header.loadfile.LoadFileController;
import com.main.stepper.admin.resources.fxml.tabs.executionshistory.tab.ExecutionHistoryScreenController;
import com.main.stepper.admin.resources.fxml.tabs.statistics.tab.StatisticsScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RootController {
    private Stage primaryStage;
    @FXML GridPane root;
    @FXML TabPane tabs;
    @FXML Tab usersManagementTab;
    @FXML Tab rolesManagementTab;
    @FXML Tab executionsHistoryTab;
    @FXML Tab statisticsTab;
    @FXML LoadFileController loadFileController;
    @FXML LoadCSSController loadCSSController;
    @FXML ExecutionHistoryScreenController flowExecutionHistoryController;
    @FXML StatisticsScreenController statisticsScreenController;

    public RootController() {
    }

    @FXML public void initialize(){
        // set root controller for sub controllers
        loadFileController.setRootController(this);
        loadCSSController.setRootController(this);
        // select initial window
        tabs.getSelectionModel().select(usersManagementTab);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
