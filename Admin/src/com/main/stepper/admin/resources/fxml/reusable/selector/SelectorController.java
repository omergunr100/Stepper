package com.main.stepper.admin.resources.fxml.reusable.selector;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.users.UserData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.script.Bindings;
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
        loadOptions(null);

        // add listener for selector change
        selection.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                selectedUser.set(newValue);
            }
            else {
                selectedUser.set("");
            }
        });

        // add listener for change in results to update unique user names for filtering
        PropertiesManager.userDataList.addListener((ListChangeListener<? super UserData>) c -> {
            List<String> uniqueUsers;
            if (PropertiesManager.userDataList.isEmpty())
                loadOptions(null);
            else {
                synchronized (PropertiesManager.userDataList) {
                    uniqueUsers = PropertiesManager.userDataList.stream().map(UserData::name).collect(Collectors.toList());
                }
                loadOptions(uniqueUsers);
            }
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
