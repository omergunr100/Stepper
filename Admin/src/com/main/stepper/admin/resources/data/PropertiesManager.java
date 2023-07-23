package com.main.stepper.admin.resources.data;

import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import com.main.stepper.shared.structures.users.UserData;
import com.main.stepper.statistics.dto.StatDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;

public class PropertiesManager {
    // HTTP client
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    // JavaFX properties
    public static final SimpleObjectProperty<Stage> primaryStage = new SimpleObjectProperty<>();

    // General use properties updated from server
    public static final SimpleBooleanProperty health = new SimpleBooleanProperty(true);
    public static final SimpleStringProperty lastLoadedFile = new SimpleStringProperty("");
    public static final ObservableList<Role> rolesList = FXCollections.observableArrayList();
    public static final SimpleObjectProperty<Role> localRole = new SimpleObjectProperty<>(null);
    public static final ObservableList<UserData> userDataList = FXCollections.observableArrayList();
    public static final ObservableList<UserData> onlineUsers = FXCollections.observableArrayList();
    public static final SimpleBooleanProperty userUpdated = new SimpleBooleanProperty(false);
    public static final ObservableList<FlowInfoDTO> flowInformationList = FXCollections.observableArrayList();
    public static final ObservableList<FlowRunResultDTO> flowRunResults = FXCollections.observableArrayList();
    public static final ObservableList<StepRunResultDTO> stepRunResults = FXCollections.observableArrayList();

    // user management screen properties
    public static final SimpleObjectProperty<UserData> selectedUser = new SimpleObjectProperty<>();

    // role management screen properties
    public static final SimpleObjectProperty<Role> selectedRole = new SimpleObjectProperty<>();

    // execution history screen properties
    public static final SimpleStringProperty executionHistorySelectedUser = new SimpleStringProperty("");
    public static final SimpleObjectProperty<FlowRunResultDTO> executionHistorySelectedFlow = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<StepRunResultDTO> executionHistorySelectedStep = new SimpleObjectProperty<>();

    // statistics screen properties
    public static final SimpleStringProperty statisticsSelectedUser = new SimpleStringProperty("");
    public static final ObservableList<StatDTO> filteredFlowStatDTOs = FXCollections.observableArrayList();
    public static final ObservableList<StatDTO> filteredStepStatDTOs = FXCollections.observableArrayList();
}
