package com.main.stepper.client.resources.data;

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

    public static SimpleBooleanProperty health = new SimpleBooleanProperty(false);
    public static SimpleStringProperty userName = new SimpleStringProperty();
    public static SimpleBooleanProperty isManager = new SimpleBooleanProperty();
    public static ObservableList<Role> roles = FXCollections.observableArrayList();
    public static SimpleBooleanProperty rolesUpdated = new SimpleBooleanProperty(false);
    public static ObservableList<UUID> flowExecutionHistory = FXCollections.observableArrayList();

    public static ObservableList<FlowInfoDTO> flowInformationList = FXCollections.observableArrayList();

    public static ObservableList<FlowRunResultDTO> flowRunResults = FXCollections.observableArrayList();
    public static ObservableList<StepRunResultDTO> stepRunResults = FXCollections.observableArrayList();

    public static SimpleObjectProperty<FlowInfoDTO> currentFlow = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<ExecutionUserInputsDTO> currentFlowExecutionUserInputs = new SimpleObjectProperty<>();

    // execution screen properties
    public static SimpleObjectProperty<FlowInfoDTO> executionSelectedFlow = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<FlowRunResultDTO> executionRunningFlow = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<StepRunResultDTO> executionSelectedStep = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<FlowRunResultDTO> continuation = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<UUID> currentRunningFlowUUID = new SimpleObjectProperty<>();
    // execution history screen properties
    public static SimpleObjectProperty<FlowRunResultDTO> executionHistorySelectedFlow = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<StepRunResultDTO> executionHistorySelectedStep = new SimpleObjectProperty<>();
}
