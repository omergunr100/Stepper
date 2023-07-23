package com.main.stepper.client.resources.fxml.reusable.selector;

import com.main.stepper.client.resources.data.PropertiesManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;
import java.util.stream.Collectors;

public class SelectorController {
    @FXML private ComboBox<String> selection;

    private SimpleStringProperty selectedUser = new SimpleStringProperty("");

    public SelectorController() {
    }

    public void setProperty(SimpleStringProperty selectedUser) {
        selectedUser.bind(this.selectedUser);
    }

    @FXML public void initialize() {
        // initialize selector
        if (PropertiesManager.isManager.not().get()) {
            selection.setDisable(true);
            loadOptions(null);
        }
        else {
            selection.setDisable(false);
            loadOptions(PropertiesManager.usernamesList);
        }

        // add listener for selector change
        selection.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                selectedUser.set(newValue);
            }
            else {
                selectedUser.set("");
            }
        });

        // add listener for manager status
        PropertiesManager.isManager.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selection.setDisable(false);
            }
            else {
                selection.getSelectionModel().clearSelection();
                selectedUser.set("");
                selection.setDisable(true);
            }
        });

        // add listener for change in results to update unique usernames for filtering
        PropertiesManager.usernamesList.addListener((ListChangeListener<? super String>) c -> {
            loadOptions((List<String>) c.getList());
        });
    }

    public void loadOptions(List<String> values) {
        if (values == null || values.isEmpty()) {
            selection.getItems().clear();
            selection.getItems().add("");
            selectedUser.set("");
        }
        else if (selection.getItems().isEmpty()) {
            selection.getItems().add("");
            selection.getItems().addAll(values);
        }
        else {
            List<String> toRemove = selection.getItems().stream().filter(s -> !s.equals("") && !values.contains(s)).collect(Collectors.toList());
            List<String> toAdd = values.stream().filter(s -> !selection.getItems().contains(s)).collect(Collectors.toList());

            if (toRemove.contains(selectedUser.get()))
                selectedUser.set("");

            selection.getItems().removeAll(toRemove);
            selection.getItems().addAll(toAdd);
        }
    }
}
