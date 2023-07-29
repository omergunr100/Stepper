package com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ComponentDragController {
    @FXML private VBox root;

    public ComponentDragController() {
    }

    @FXML public void initialize() {
    }

    public void loadSteps() {
        // todo: for each step in registry, create a new DraggableStep component and add to root
    }

    public void onDragDetected() {
        // todo: if done on one of the DraggableSteps, copy the step and drag it along
    }
}
