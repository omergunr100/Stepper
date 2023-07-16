package com.main.stepper.admin.resources.fxml.tabs.roles.table;

import com.main.stepper.shared.structures.roles.Role;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static com.main.stepper.admin.resources.data.PropertiesManager.rolesList;
import static com.main.stepper.admin.resources.data.PropertiesManager.selectedRole;

public class RolesTableController {
    @FXML private TableView<Role> rolesTable;

    public RolesTableController() {
    }

    @FXML public void initialize(){
        // bind to properties
        Bindings.bindContent(rolesTable.getItems(), rolesList);

        // add listener for selection
        rolesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedRole.set(newValue);
        });

        // setup table columns
        TableColumn<Role, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
        nameCol.setPrefWidth(100);

        TableColumn<Role, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().description()));
        descriptionCol.setPrefWidth(200);

        rolesTable.getColumns().addAll(nameCol, descriptionCol);
    }

}
