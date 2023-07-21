package com.main.stepper.admin.resources.fxml.tabs.users.edit;

import com.google.gson.Gson;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.admin.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
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
import java.util.stream.Collectors;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class UserEditController {
    @FXML private GridPane root;
    @FXML private TextField userNameTextField;
    @FXML private CheckBox isManagerCheckBox;
    @FXML private TableView<RoleCheck> rolesTable;
    @FXML private Button applyChangesButton;
    @FXML private TableView<FlowRunCounter> userFlowRunsTable;

    private final ObservableList<RoleCheck> roleChecksList = FXCollections.observableArrayList();
    private final ObservableList<Role> selectedRoles = FXCollections.observableArrayList();

    private final ObservableList<FlowRunCounter> userFlowRunsList = FXCollections.observableArrayList();
    private final ObservableList<String> userSelectedFlows = FXCollections.observableArrayList();

    public UserEditController() {
    }

    private void setupRoleCheckTable() {
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

        // setup table of roles
        TableColumn<RoleCheck, Boolean> isSelectedCol = new TableColumn<>("Selected");
        isSelectedCol.setCellValueFactory(param -> param.getValue().selected);
        isSelectedCol.setCellFactory(param -> new CheckBoxTableCell<>());
        isSelectedCol.setPrefWidth(100);
        isSelectedCol.setSortable(false);

        TableColumn<RoleCheck, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(param -> param.getValue().name);
        nameCol.setPrefWidth(100);
        nameCol.setSortable(false);

        TableColumn<RoleCheck, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().description);
        descriptionCol.setPrefWidth(200);
        descriptionCol.setSortable(false);

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
            while(c.next()) {
                c.getRemoved().forEach(role -> roleChecksList.removeIf(roleCheck -> roleCheck.name.get().equals(role.name())));
                c.getAddedSubList().forEach(role -> {
                    RoleCheck roleCheck = new RoleCheck(role, false);
                    roleCheck.listForSelected(selectedRoles);
                    roleChecksList.add(roleCheck);
                });
            }
            rolesTable.setItems(roleChecksList);
            rolesTable.refresh();
        });
    }

    private void setupFlowRunTable() {
        // setup table columns
        TableColumn<FlowRunCounter, Boolean> isSelectedCol = new TableColumn<>("Enabled");
        isSelectedCol.setCellValueFactory(param -> param.getValue().enabled);
        isSelectedCol.setCellFactory(param -> new CheckBoxTableCell<>());
        isSelectedCol.setPrefWidth(100);

        TableColumn<FlowRunCounter, String> flowNameCol = new TableColumn<>("Name");
        flowNameCol.setCellValueFactory(param -> param.getValue().name);
        flowNameCol.setPrefWidth(150);

        TableColumn<FlowRunCounter, String> flowDescCol = new TableColumn<>("Description");
        flowDescCol.setCellValueFactory(param -> param.getValue().description);
        flowDescCol.setPrefWidth(200);

        TableColumn<FlowRunCounter, Integer> flowRunCount = new TableColumn<>("Times Run");
        flowRunCount.setCellValueFactory(param -> param.getValue().timesRun.asObject());
        flowRunCount.setPrefWidth(100);

        userFlowRunsTable.getColumns().addAll(
                isSelectedCol,
                flowNameCol,
                flowDescCol,
                flowRunCount
        );

        userFlowRunsTable.setItems(userFlowRunsList);

        // add listeners to update userFlowRunsList
        flowInformationList.addListener((ListChangeListener<? super FlowInfoDTO>) c -> {
            while(c.next()) {
                c.getRemoved().forEach(flowInfoDTO -> userFlowRunsList.removeIf(flowRunCounter -> flowRunCounter.name.get().equals(flowInfoDTO.name())));
                c.getAddedSubList().forEach(flowInfoDTO -> {
                    FlowRunCounter flowRunCounter = new FlowRunCounter(flowInfoDTO);
                    flowRunCounter.listForSelected(userSelectedFlows);
                    userFlowRunsList.add(flowRunCounter);
                });
            }
            userFlowRunsTable.setItems(userFlowRunsList);
            userFlowRunsTable.refresh();
        });

        // add listeners to update flow counts
        // reset list on user change
        selectedUser.addListener((observable, oldValue, newValue) -> {
                userFlowRunsList.forEach(flowRunCounter -> {
                    if (newValue == null)
                        flowRunCounter.timesRun.set(0);
                    else {
                        flowRunCounter.timesRun.set(
                                (int) flowRunResults.stream()
                                        .filter(result -> result.user().equals(newValue.name()) && result.name().equals(flowRunCounter.name.get()))
                                        .count()
                        );
                    }
                    userFlowRunsTable.refresh();
                });
        });
        // change list based on new results
        flowRunResults.addListener((ListChangeListener<? super FlowRunResultDTO>) c -> {
            while(c.next()) {
                c.getRemoved().forEach(flowRunResult -> {
                    userFlowRunsList.forEach(flowRunCounter -> {
                        if (flowRunCounter.name.get().equals(flowRunResult.name())) {
                            flowRunCounter.timesRun.set(flowRunCounter.timesRun.get() - 1);
                        }
                    });
                });
                c.getAddedSubList().forEach(flowRunResult -> {
                    userFlowRunsList.forEach(flowRunCounter -> {
                        if (flowRunCounter.name.get().equals(flowRunResult.name())) {
                            flowRunCounter.timesRun.set(flowRunCounter.timesRun.get() + 1);
                        }
                    });
                });
            }
            userFlowRunsTable.refresh();
        });
    }

    @FXML public void initialize() {
        setupRoleCheckTable();

        setupFlowRunTable();

        // listener on selected user to update this
        selectedUser.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                userNameTextField.setText(newValue.name());
                isManagerCheckBox.setSelected(newValue.isManager());
                selectedRoles.setAll(newValue.roles());
                rolesTable.refresh();
                if (newValue.isManager()) {
                    userSelectedFlows.setAll(flowInformationList.stream().map(FlowInfoDTO::name).collect(Collectors.toList()));
                }
                else {
                    userSelectedFlows.setAll(Role.uniqueUnionGroup(newValue.roles()));
                }
                userFlowRunsTable.refresh();
                root.setDisable(false);
                root.setVisible(true);
            }
            else {
                selectedRoles.clear();
                userSelectedFlows.clear();
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
                    if (response.code() == 404) {
                        Platform.runLater(() -> new ErrorPopup("User not found, make sure the user is still in the system."));
                    }
                    response.close();
                }
            });
        }
    }
}
