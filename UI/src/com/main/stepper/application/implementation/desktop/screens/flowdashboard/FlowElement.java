package com.main.stepper.application.implementation.desktop.screens.flowdashboard;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FlowElement extends VBox {
    public FlowElement(String name, String description) {
        super();
        Label fName = new Label("Flow name: " + name);
        Label fDescription = new Label("Flow description: " + description);
        this.getChildren().addAll(fName, fDescription);
    }
}
