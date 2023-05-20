package com.main.stepper.application.resources.fxml.root;

import com.main.stepper.application.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.application.resources.fxml.header.loadfile.LoadFileController;
import com.main.stepper.application.resources.fxml.tabs.flowsdefinition.FlowsDefinitionController;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.definition.implementation.DesktopEngine;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RootController {
    private IEngine engine;
    private Stage primaryStage;
    @FXML GridPane root;
    @FXML LoadFileController loadFileController;
    @FXML LoadCSSController loadCSSController;
    @FXML FlowsDefinitionController flowsDefinitionController;

    public RootController() {
    }

    @FXML public void initialize(){
        this.engine = new DesktopEngine();

        this.loadFileController.setRootController(this);
        this.loadCSSController.setRootController(this);
        this.flowsDefinitionController.setRootController(this);
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
        flowsDefinitionController.updateFlows();
        root.setDisable(false);
    }

    public void executeFlow(IFlowDefinition currentFlow) {

    }
}
