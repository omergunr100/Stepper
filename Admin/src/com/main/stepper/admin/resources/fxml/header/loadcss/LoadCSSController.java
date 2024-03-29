package com.main.stepper.admin.resources.fxml.header.loadcss;

import com.main.stepper.admin.resources.css.CSSRegistry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.main.stepper.admin.resources.data.PropertiesManager.primaryStage;

public class LoadCSSController {
    @FXML private ChoiceBox<String> choice;

    public LoadCSSController() {
    }

    @FXML public void initialize(){
        choice.setItems(
                FXCollections.observableArrayList(
                        Arrays.stream(CSSRegistry.values()).map(CSSRegistry::getName).collect(Collectors.toList())
                )
        );
        choice.getSelectionModel().selectFirst();
        choice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChange(newValue));
    }

    public void onChange(String newValue) {
        primaryStage.get().getScene().getStylesheets().clear();
        CSSRegistry value = Arrays.stream(CSSRegistry.values()).filter(cssRegistry -> cssRegistry.getName().equals(newValue)).findFirst().orElse(null);
        if(value == null){
            return;
        }
        URL url = CSSRegistry.class.getResource(value.getFile().getPath());
        primaryStage.get().getScene().getStylesheets().add(url.toExternalForm());
    }
}
