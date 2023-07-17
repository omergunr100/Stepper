package com.main.stepper.admin.resources.fxml.tabs.roles.edit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.admin.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.users.UserData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class RolesEditController {
    @FXML private GridPane root;
    @FXML private TextField nameTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private TableView<FlowCheck> flowInfoTable;

    private ObservableList<FlowCheck> flowChecks = FXCollections.observableArrayList();
    private ObservableList<FlowInfoDTO> selectedFlows = FXCollections.observableArrayList();

    public RolesEditController() {
    }

    @FXML public void initialize() {
        // force initialize flow checks
        if (flowChecks.isEmpty() && !flowInformationList.isEmpty()) {
            synchronized (flowInformationList) {
                flowInformationList.forEach(flowInfoDTO -> {
                    FlowCheck flowCheck = new FlowCheck(flowInfoDTO, false);
                    flowChecks.add(flowCheck);
                });
            }
            flowInfoTable.setItems(flowChecks);
            flowInfoTable.refresh();
        }

        // initialize table columns
        TableColumn<FlowCheck, Boolean> isSelectedCol = new TableColumn<>("Selected");
        isSelectedCol.setCellValueFactory(param -> param.getValue().selected);
        isSelectedCol.setCellFactory(param -> new CheckBoxTableCell<>());
        isSelectedCol.setPrefWidth(100);

        TableColumn<FlowCheck, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(param -> param.getValue().name);
        nameCol.setPrefWidth(100);

        TableColumn<FlowCheck, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().description);
        descriptionCol.setPrefWidth(200);

        flowInfoTable.getColumns().addAll(isSelectedCol, nameCol, descriptionCol);
        nameCol.setEditable(false);
        descriptionCol.setEditable(false);

        // disable row selection
        flowInfoTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                flowInfoTable.getSelectionModel().clearSelection();
        });

        // listener on flows to update flow checks
        flowInformationList.addListener((ListChangeListener<? super FlowInfoDTO>) c -> {
            c.next();
            c.getRemoved().forEach(flow -> flowChecks.removeIf(flowCheck -> flowCheck.name.get().equals(flow.name())));
            c.getAddedSubList().forEach(flow -> {
                FlowCheck flowCheck = new FlowCheck(flow, false);
                flowCheck.listForSelected(selectedFlows);
                flowChecks.add(flowCheck);
            });
            flowInfoTable.setItems(flowChecks);
            flowInfoTable.refresh();
        });

        // add listener for selected role
        selectedRole.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                root.setDisable(true);
                root.setVisible(false);
                flowInfoTable.getItems().clear();
            }
            else {
                nameTextField.setText(newValue.name());
                if (newValue.isLocal())
                    nameTextField.setEditable(true);
                else
                    nameTextField.setEditable(false);
                descriptionTextArea.setText(newValue.description());
                synchronized (flowInformationList) {
                    if (flowInformationList.isEmpty())
                        selectedFlows.clear();
                    else
                        selectedFlows.setAll(flowInformationList.stream().filter(flow -> newValue.allowedFlows().contains(flow.name())).collect(Collectors.toList()));
                }
                if (newValue.isAutomatic())
                    root.setDisable(true);
                else
                    root.setDisable(false);

                root.setVisible(true);
            }
        });
    }

    @FXML private void applyChanges() {
        if (selectedRole.get().isLocal()) {
            if (nameTextField.getText().isEmpty()) {
                new ErrorPopup("Name cannot be empty.");
                return;
            }
            else if (nameTextField.getText().equals("New Role")) {
                new ErrorPopup("Name cannot be \"New Role\".");
                return;
            }
            synchronized (rolesList) {
                Optional<Role> first = rolesList.stream().filter(role -> !role.isLocal() && role.name().equals(nameTextField.getText())).findFirst();
                if (first.isPresent()) {
                    new ErrorPopup("Role with this name already exists.");
                    return;
                }
            }
        }
        Gson gson = new Gson();
        List<String> allowedFlows = flowChecks.stream().filter(flow -> flow.selected.get()).map(flow -> flow.name.get()).collect(Collectors.toList());
        Role role = new Role(selectedRole.get().isLocal() ? nameTextField.getText() : selectedRole.get().name(), descriptionTextArea.getText(), allowedFlows, false, selectedRole.get().isLocal());
        RequestBody body = RequestBody.create(gson.toJson(role), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("isAdmin", "true")
                .url(URLManager.ROLES_MANAGEMENT)
                .put(body)
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> new ErrorPopup("Server unreachable, try again later."));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Gson gson = new Gson();
                    Boolean isLocal = gson.fromJson(response.body().string(), Boolean.class);
                    if (isLocal) {
                        Platform.runLater(() -> {
                            synchronized (rolesList) {
                                rolesList.remove(localRole.get());
                            }
                            if (selectedRole.get().isLocal()) {
                                selectedRole.set(null);
                            }
                            localRole.set(null);
                        });
                    }
                }
            });
        }
    }

    @FXML private void deleteRole() {
        if (selectedRole.get().isLocal()) {
            selectedRole.set(null);
            synchronized (rolesList) {
                rolesList.remove(localRole.get());
            }
            localRole.set(null);
        }
        else {
            Gson gson = new Gson();
            RequestBody body = RequestBody.create(gson.toJson(selectedRole.get().name()), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .addHeader("isAdmin", "true")
                    .url(URLManager.ROLES_MANAGEMENT)
                    .delete(body)
                    .build();
            synchronized (HTTP_CLIENT) {
                Call call = HTTP_CLIENT.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> new ErrorPopup("Server unreachable, try again later."));
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Gson gson = new Gson();
                        List<UserData> problems = gson.fromJson(response.body().string(), new TypeToken<ArrayList<UserData>>() {}.getType());
                        if (!problems.isEmpty()) {
                            Platform.runLater(() -> new ErrorPopup("Cannot delete role, it is used by " + problems.size() + " users."));
                        }
                        else if (response.code() == 400) {
                            Platform.runLater(() -> new ErrorPopup("There is no role with this name, ensure your data is up to date."));
                        }
                    }
                });
            }

        }
    }
}
