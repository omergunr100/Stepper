package com.main.stepper.client.resources.dataview.customtreecell;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LabelButtonTreeCellController {
    @FXML private Button button;

    public LabelButtonTreeCellController() {
    }

    public void setOnAction(Runnable action) {
        button.setOnAction(event -> action.run());
    }
}
