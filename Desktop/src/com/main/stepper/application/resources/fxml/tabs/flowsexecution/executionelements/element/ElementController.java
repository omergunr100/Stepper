package com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements.element;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ElementController {
    @FXML private HBox root;
    @FXML private Label propertyName;
    @FXML private Label propertyValue;
    @FXML private Button infoButton;

    public ElementController() {
    }

    @FXML public void initialize(){
    }

    public void setPropertyName(String propertyName) {
        this.propertyName.setText(propertyName);
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue.setText(propertyValue);
    }

    public void setOnAction(Runnable action){
        infoButton.setOnAction(event -> action.run());
    }

    public void removeButton(){
        root.getChildren().remove(infoButton);
    }
}
