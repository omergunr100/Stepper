package com.main.stepper.client.resources.fxml.reusable.executionelements.element;

import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class ElementController {
    @FXML private HBox root;
    @FXML private Label propertyName;
    @FXML private Label propertyValue;
    @FXML private Button infoButton;
    private StepRunResultDTO context;

    public ElementController() {
    }

    @FXML public void initialize(){
    }

    public void setContext(StepRunResultDTO context) {
        this.context = context;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName.setText(propertyName);
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue.setText(propertyValue);
    }

    public void setOnAction(Consumer<StepRunResultDTO> consumer){
        infoButton.setOnAction(event -> consumer.accept(context));
    }

    public void removeButton(){
        root.getChildren().remove(infoButton);
    }
}
