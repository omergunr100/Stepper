package com.main.stepper.application.resources.fxml.root;

import com.main.stepper.application.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.application.resources.fxml.header.loadfile.LoadFileController;
import com.main.stepper.application.resources.fxml.reusable.flowrundetails.FlowRunDetailsController;
import com.main.stepper.application.resources.fxml.tabs.flowsdefinition.FlowsDefinitionController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.FlowExecutionController;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.definition.implementation.DesktopEngine;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RootController {
    private IEngine engine;
    private Stage primaryStage;
    @FXML GridPane root;
    @FXML TabPane tabs;
    @FXML Tab flowsDefinitionTab;
    @FXML Tab flowsExecutionTab;
    @FXML Tab executionsHistoryTab;
    @FXML Tab statisticsTab;
    @FXML LoadFileController loadFileController;
    @FXML LoadCSSController loadCSSController;
    @FXML FlowsDefinitionController flowsDefinitionController;
    @FXML FlowExecutionController flowExecutionController;
    @FXML FlowRunDetailsController flowExecutionHistoryController;

    public RootController() {
    }

    @FXML public void initialize(){
        this.engine = new DesktopEngine();

        this.loadFileController.setRootController(this);
        this.loadCSSController.setRootController(this);
        this.flowsDefinitionController.setRootController(this);
        this.flowExecutionController.setRootController(this);

        // initialize thread for updating flow run history
        Thread updateFlowRunHistoryThread = new Thread(() -> {
            while(true) {
                // update table with history
                flowExecutionHistoryController.updateTable(engine.getFlowRuns());
                // sleep for 150m
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateFlowRunHistoryThread.setDaemon(true);
        updateFlowRunHistoryThread.start();

        // select initial window
        tabs.getSelectionModel().select(flowsDefinitionTab);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public IEngine getEngine() {
        return engine;
    }

    public void loadFlows() {
        root.setDisable(true);
        flowExecutionHistoryController.reset();
        flowExecutionController.reset();
        flowsDefinitionController.updateFlows();
        tabs.getSelectionModel().select(flowsDefinitionTab);
        root.setDisable(false);
    }

    public void executeFlow(IFlowDefinition currentFlow) {
        this.flowExecutionController.setCurrentFlow(currentFlow);
        this.tabs.getSelectionModel().select(flowsExecutionTab);
    }
}
