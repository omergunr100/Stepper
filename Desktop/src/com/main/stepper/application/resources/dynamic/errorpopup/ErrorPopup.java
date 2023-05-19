package com.main.stepper.application.resources.dynamic.errorpopup;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ErrorPopup {

    public ErrorPopup(String error) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Error encountered");

        VBox layout = new VBox();

        Label label = new Label(error);
        label.getStyleClass().add("error-label");
        layout.getChildren().add(label);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public ErrorPopup(List<String> errors) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Errors encountered");

        VBox layout = new VBox();

        for(String error : errors){
            Label label = new Label(error);
            label.getStyleClass().add("error-label");
            layout.getChildren().add(label);
        }

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
