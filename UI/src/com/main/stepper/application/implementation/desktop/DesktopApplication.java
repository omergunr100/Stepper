package com.main.stepper.application.implementation.desktop;

import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.definition.implementation.DesktopEngine;
import javafx.application.Application;
import javafx.stage.Stage;

public class DesktopApplication extends Application {
    private IEngine engine;

    public DesktopApplication(){
        this.engine = new DesktopEngine();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
