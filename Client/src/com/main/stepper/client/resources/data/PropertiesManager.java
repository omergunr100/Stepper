package com.main.stepper.client.resources.data;

import com.main.stepper.shared.structures.chat.message.Message;
import com.main.stepper.shared.structures.executionuserinputs.ExecutionUserInputsDTO;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okhttp3.OkHttpClient;

import java.util.UUID;

public class PropertiesManager {
    public static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .cookieJar(new CookieManager())
            .build();

    public static final SimpleBooleanProperty health = new SimpleBooleanProperty(false);
    public static final SimpleStringProperty userName = new SimpleStringProperty();
    public static final SimpleBooleanProperty isManager = new SimpleBooleanProperty();
    public static final ObservableList<Role> roles = FXCollections.observableArrayList();
    public static final ObservableList<UUID> flowExecutionHistory = FXCollections.observableArrayList();
    public static final ObservableList<String> usernamesList = FXCollections.observableArrayList();

    public static final ObservableList<FlowInfoDTO> flowInformationList = FXCollections.observableArrayList();

    public static final ObservableList<FlowRunResultDTO> flowRunResults = FXCollections.observableArrayList();
    public static final ObservableList<StepRunResultDTO> stepRunResults = FXCollections.observableArrayList();

    public static final SimpleObjectProperty<FlowInfoDTO> currentFlow = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<ExecutionUserInputsDTO> currentFlowExecutionUserInputs = new SimpleObjectProperty<>();

    // role filter
    public static final SimpleStringProperty roleFilter = new SimpleStringProperty("");

    // execution screen properties
    public static final SimpleObjectProperty<FlowInfoDTO> executionSelectedFlow = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<FlowRunResultDTO> executionRunningFlow = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<StepRunResultDTO> executionSelectedStep = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<FlowRunResultDTO> continuation = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<UUID> currentRunningFlowUUID = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<FlowRunResultDTO> reloadFlow = new SimpleObjectProperty<>(null);
    public static final SimpleBooleanProperty executionIsFlowSelected = new SimpleBooleanProperty(false);

    // execution history screen properties
    public static final SimpleStringProperty executionHistorySelectedUser = new SimpleStringProperty("");
    public static final SimpleObjectProperty<FlowRunResultDTO> executionHistorySelectedFlow = new SimpleObjectProperty<>();
    public static final SimpleObjectProperty<StepRunResultDTO> executionHistorySelectedStep = new SimpleObjectProperty<>();

    // chat properties
    public static final ObservableList<Message> chatMessages = FXCollections.observableArrayList();
}
