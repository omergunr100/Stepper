package com.main.stepper.client.resources.fxml.tabs.statistics.selector;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class SelectorController {
    private List<String> values;
    @FXML private ComboBox<String> selection;
    public SelectorController() {
    }

    @FXML public void initialize() {
        loadOptions(null);
    }

    public void loadOptions(List<String> values) {
        this.values = values;
        if(values != null) {
            selection.getItems().addAll(values);
            selection.setDisable(false);
        }
        else {
            selection.getItems().clear();
            selection.setDisable(true);
        }
    }

    public String getSelection() {
        return selection.getValue();
    }
}
