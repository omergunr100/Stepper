package com.main.stepper.admin.resources.fxml.tabs.users.edit;

import com.main.stepper.shared.structures.roles.Role;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Optional;

public class RoleCheck {
    public SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleStringProperty description = new SimpleStringProperty();
    public ObservableList<String> allowedFlows = FXCollections.observableArrayList();

    public RoleCheck(Role role, Boolean selected) {
        this.name.set(role.name());
        this.description.set(role.description());
        this.allowedFlows.addAll(role.allowedFlows());
        this.selected.set(selected);
    }

    public void listForSelected(ObservableList<Role> userRolesList) {
        userRolesList.addListener((ListChangeListener<Role>) c -> {
            c.next();
            ObservableList<? extends Role> list = c.getList();
            synchronized (list) {
                Optional<? extends Role> first = list.stream().filter(role -> role.name().equals(name.get())).findFirst();
                if (first.isPresent())
                    selected.set(true);
                else
                    selected.set(false);
            }
        });
    }
}
