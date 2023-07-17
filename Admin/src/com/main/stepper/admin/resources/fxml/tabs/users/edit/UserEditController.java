package com.main.stepper.admin.resources.fxml.tabs.users.edit;

import com.google.gson.Gson;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.admin.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.users.UserData;
import com.main.stepper.shared.structures.wrappers.RolesAssignmentWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class UserEditController {
    @FXML private GridPane root;
    @FXML private TextField userNameTextField;
    @FXML private CheckBox isManagerCheckBox;
    @FXML private TableView<RoleCheck> rolesTable;
    @FXML private Button applyChangesButton;

    private ObservableList<RoleCheck> roleChecksList = FXCollections.observableArrayList();
    private ObservableList<Role> selectedRoles = FXCollections.observableArrayList();

    public UserEditController() {
    }

    @FXML public void initialize() {
        // force setup rolesCheckList
        if (roleChecksList.isEmpty() && !rolesList.isEmpty()) {
            synchronized (rolesList) {
                rolesList.forEach(role -> {
                    RoleCheck roleCheck = new RoleCheck(role, false);
                    roleChecksList.add(roleCheck);
                });
            }
            rolesTable.setItems(roleChecksList);
            rolesTable.refresh();
        }

        // setup table
        TableColumn<RoleCheck, Boolean> isSelectedCol = new TableColumn<>("Selected");
        isSelectedCol.setCellValueFactory(param -> param.getValue().selected);
        isSelectedCol.setCellFactory(param -> new CheckBoxTableCell<>());
        isSelectedCol.setPrefWidth(100);

        TableColumn<RoleCheck, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(param -> param.getValue().name);
        nameCol.setPrefWidth(100);

        TableColumn<RoleCheck, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().description);
        descriptionCol.setPrefWidth(200);

        rolesTable.getColumns().addAll(isSelectedCol, nameCol, descriptionCol);
        nameCol.setEditable(false);
        descriptionCol.setEditable(false);

        // disable row selection
        rolesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                rolesTable.getSelectionModel().clearSelection();
            }
        });

        // listener on rolesList to update roleChecksList
        rolesList.addListener((ListChangeListener<? super Role>) c -> {
            c.next();
            c.getRemoved().forEach(role -> roleChecksList.removeIf(roleCheck -> roleCheck.name.get().equals(role.name())));
            c.getAddedSubList().forEach(role -> {
                RoleCheck roleCheck = new RoleCheck(role, false);
                roleCheck.listForSelected(selectedRoles);
                roleChecksList.add(roleCheck);
            });
            rolesTable.setItems(roleChecksList);
            rolesTable.refresh();
        });

        // listener on selected user to update this
        selectedUser.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                userNameTextField.setText(newValue.name());
                isManagerCheckBox.setSelected(newValue.isManager());
                selectedRoles.setAll(newValue.roles());
                rolesTable.refresh();
                root.setDisable(false);
                root.setVisible(true);
            }
            else {
                selectedRoles.clear();
                root.setDisable(true);
                root.setVisible(false);
            }
        });

    }

    @FXML private void applyChange() {
        Gson gson = new Gson();
        UserData data = new UserData(selectedUser.get().name());
        data.setManager(isManagerCheckBox.isSelected());
        RequestBody body = RequestBody.create(gson.toJson(data), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(URLManager.SET_MANAGER)
                .addHeader("isAdmin", "true")
                .put(body)
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // error popup will appear for other message
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    response.close();
                }
            });
        }

        List<String> roles = new ArrayList<>();
        roleChecksList.forEach(roleCheck -> {
            if (roleCheck.selected.get()) {
                roles.add(roleCheck.name.get());
            }
        });
        RolesAssignmentWrapper raw = new RolesAssignmentWrapper(selectedUser.get().name(), roles);

        body = RequestBody.create(gson.toJson(raw), MediaType.parse("application/json"));
        request = new Request.Builder()
                .url(URLManager.ROLES_ASSIGNMENT)
                .addHeader("isAdmin", "true")
                .put(body)
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> new ErrorPopup("Server unreachable, please try again later."));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() == 200) {
                    }
                    else if (response.code() == 404) {
                        Platform.runLater(() -> new ErrorPopup("User not found, make sure the user is still in the system."));
                    }
                    response.close();
                }
            });
        }
    }
}
