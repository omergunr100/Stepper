package com.main.stepper.application.implementation;

import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.definition.implementation.ConsoleEngine;
import javafx.stage.Stage;

public class MainAppController {
    private IEngine engine;
    private Stage primaryStage;

    public MainAppController() {
        // TODO: change to desktop engine
        this.engine = new ConsoleEngine();
    }

    public IEngine getEngine() {
        return engine;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
