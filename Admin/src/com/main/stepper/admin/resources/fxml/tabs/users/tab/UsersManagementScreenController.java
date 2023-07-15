package com.main.stepper.admin.resources.fxml.tabs.users.tab;

import com.main.stepper.admin.resources.fxml.tabs.users.edit.UserEditController;
import com.main.stepper.admin.resources.fxml.tabs.users.table.UsersTableController;
import javafx.fxml.FXML;

public class UsersManagementScreenController {
    @FXML private UsersTableController usersTableController;
    @FXML private UserEditController userEditController;

    public UsersManagementScreenController() {
    }
}
