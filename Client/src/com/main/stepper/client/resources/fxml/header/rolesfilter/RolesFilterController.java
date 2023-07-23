package com.main.stepper.client.resources.fxml.header.rolesfilter;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.shared.structures.roles.Role;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.util.List;
import java.util.stream.Collectors;

public class RolesFilterController {
    @FXML private ChoiceBox<String> selector;

    public RolesFilterController() {
    }

    @FXML public void initialize(){
        // initialize selector
        selector.getItems().add("");
        if (selector.getItems().size() == 1 && PropertiesManager.roles.size() > 0)
            selector.getItems().addAll(PropertiesManager.roles.stream().map(Role::name).collect(Collectors.toList()));

        // add listeners to update the filter
        selector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            PropertiesManager.roleFilter.set(newValue);
        });

        // add listener to update the choices
        PropertiesManager.roles.addListener((ListChangeListener<? super Role>) c -> {
            while(c.next()) {
                List<? extends Role> removed = c.getRemoved();
                List<? extends Role> added = c.getAddedSubList();

                if (removed.contains(PropertiesManager.roleFilter.get()))
                    PropertiesManager.roleFilter.set("");

                removed.stream().map(Role::name).forEach(selector.getItems()::remove);
                added.stream().map(Role::name).forEach(selector.getItems()::add);
            }
        });
    }
}
