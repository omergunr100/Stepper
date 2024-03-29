package com.main.stepper.admin.resources.fxml.tabs.users.table;

import com.main.stepper.shared.structures.users.UserData;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class UsersTableController {
    @FXML private TableView<UserData> table;

    public UsersTableController() {
    }

    public void initialize() {
        // make table columns
        TableColumn<UserData, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));
        nameCol.setPrefWidth(100);

        TableColumn<UserData, Boolean> isManagerCol = new TableColumn<>("Is Manager");
        isManagerCol.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().isManager()));
        isManagerCol.setCellFactory(param -> new CheckBoxTableCell<>());

        table.getColumns().addAll(nameCol, isManagerCol);

        // bind to properties
        Bindings.bindContent(table.getItems(), onlineUsers);
        userUpdated.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue) {
                table.refresh();
                userUpdated.set(false);
            }
        });

        // listen for selected user changes
        selectedUser.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                table.getSelectionModel().clearSelection();
            }
        });

        // change selected user based on table selection
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedUser.set(newValue);
        });

        // listen for users list indirect updates
        userUpdated.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue) {
                table.refresh();
                userUpdated.set(false);
            }
        });
    }
}
