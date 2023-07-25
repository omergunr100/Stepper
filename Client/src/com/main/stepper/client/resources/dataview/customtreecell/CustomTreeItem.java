package com.main.stepper.client.resources.dataview.customtreecell;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CustomTreeItem extends HBox {
    private Label label;
    private Button button;

    public CustomTreeItem() {
        this("");
    }

    public CustomTreeItem(String labelText) {
        this.setSpacing(10);
        label = new Label(labelText);
        getChildren().add(label);
    }

    public CustomTreeItem(String labelText, String buttonText, Runnable onAction) {
        this(labelText);
        button = new Button(buttonText);
        button.setOnAction(event -> onAction.run());
        getChildren().add(button);
    }

}
