package com.main.stepper.application.resources.fxml.reusable.flowinput;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.io.api.IDataIO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FlowInputController {
    private IDataIO input;
    @FXML private VBox root;
    @FXML private Label nameLabel;
    @FXML private Label userStringLabel;
    @FXML private Label necessityLabel;
    @FXML private Label typeLabel;
    @FXML private TextField valueField;

    public FlowInputController() {
    }

    public void init(IDataIO input) {
        this.input = input;
        nameLabel.setText(input.getName());
        userStringLabel.setText(input.getUserString());
        necessityLabel.setText(input.getNecessity().toString());
        typeLabel.setText(input.getDataDefinition().getName());
    }

    public IDataIO input() {
        return input;
    }

    public String getValue(){
        return valueField.getText();
    }

    public void setValue(String value) {
        valueField.setText(value);
    }

    public void setVisible(boolean visible) {
        root.setVisible(visible);
    }
}
