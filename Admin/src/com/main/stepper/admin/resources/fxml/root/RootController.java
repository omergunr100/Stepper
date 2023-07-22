package com.main.stepper.admin.resources.fxml.root;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.admin.resources.fxml.header.loadfile.LoadFileController;
import com.main.stepper.admin.resources.fxml.tabs.executionshistory.tab.ExecutionHistoryScreenController;
import com.main.stepper.admin.resources.fxml.tabs.roles.tab.RolesManagementScreenController;
import com.main.stepper.admin.resources.fxml.tabs.statistics.StatisticsScreenController;
import com.main.stepper.admin.resources.fxml.tabs.users.tab.UsersManagementScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;

public class RootController {
    // root
    @FXML private GridPane root;
    // header elements
    @FXML private LoadCSSController loadCSSController;
    @FXML private LoadFileController loadFileHeaderController;
    // tabs
    @FXML private TabPane tabs;
    @FXML private Tab usersManagementTab;
    @FXML private Tab rolesManagementTab;
    @FXML private Tab executionsHistoryTab;
    @FXML private Tab statisticsTab;
    // tab screen controllers
    @FXML private UsersManagementScreenController usersManagementScreenController;
    @FXML private RolesManagementScreenController rolesManagementScreenController;
    @FXML private ExecutionHistoryScreenController flowExecutionHistoryController;
    @FXML private StatisticsScreenController statisticsScreenController;

    public RootController() {
    }

    @FXML public void initialize(){
        // bind to properties
        PropertiesManager.health.addListener((observable, oldValue, newValue) -> {
            root.setDisable(!newValue);
        });

        // select initial window
        tabs.getSelectionModel().select(usersManagementTab);
    }
}
