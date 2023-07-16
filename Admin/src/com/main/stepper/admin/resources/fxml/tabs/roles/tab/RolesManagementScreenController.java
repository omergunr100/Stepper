package com.main.stepper.admin.resources.fxml.tabs.roles.tab;

import com.main.stepper.admin.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.admin.resources.fxml.tabs.roles.edit.RolesEditController;
import com.main.stepper.admin.resources.fxml.tabs.roles.table.RolesTableController;
import com.main.stepper.shared.structures.roles.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static com.main.stepper.admin.resources.data.PropertiesManager.localRole;

public class RolesManagementScreenController {
    @FXML private RolesTableController rolesTableController;
    @FXML private RolesEditController rolesEditController;
    @FXML private Button addRoleButton;

    public RolesManagementScreenController() {
    }

    @FXML private void addRole() {
        if (localRole.isNull().get()) {
            Role role = new Role("New Role", "New Role Description");
            localRole.set(role);
        }
        else {
            new ErrorPopup("There is already a new role to edit!");
        }
    }
}
