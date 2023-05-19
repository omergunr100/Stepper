package com.main.stepper.application.resources.fxml.header.loadcss;

import com.main.stepper.application.resources.css.CSSRegistry;
import com.main.stepper.application.resources.fxml.root.RootController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.net.URL;

public class LoadCSSController {
    @FXML private RootController rootController;
    @FXML private ChoiceBox<CSSRegistry> choice;

    public LoadCSSController() {
    }

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML public void initialize(){
        choice.setItems(
                FXCollections.observableArrayList(
                        CSSRegistry.values()
                )
        );
        choice.getSelectionModel().selectFirst();
        choice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChange(newValue));
    }

    private void onChange(CSSRegistry newValue) {
        rootController.getPrimaryStage().getScene().getStylesheets().clear();
        if(newValue == CSSRegistry.DEFAULT){
            return;
        }
        URL url = CSSRegistry.class.getResource(newValue.getFile().getPath());
        rootController.getPrimaryStage().getScene().getStylesheets().add(url.toExternalForm());
    }
}
