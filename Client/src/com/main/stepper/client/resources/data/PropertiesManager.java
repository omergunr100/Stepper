package com.main.stepper.client.resources.data;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.shared.structures.executionuserinputs.ExecutionUserInputsDTO;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.roles.Role;
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

    public static SimpleStringProperty userName = new SimpleStringProperty();
    public static SimpleBooleanProperty isManager = new SimpleBooleanProperty();
    public static ObservableList<Role> roles = FXCollections.observableArrayList();
    public static ObservableList<UUID> flowExecutionHistory = FXCollections.observableArrayList();

    public static ObservableList<FlowInfoDTO> flowInformationList = FXCollections.observableArrayList();

    public static ObservableList<IFlowRunResult> flowRunResults = FXCollections.observableArrayList();
    public static ObservableList<IStepRunResult> stepRunResults = FXCollections.observableArrayList();

    public static SimpleObjectProperty<FlowInfoDTO> currentFlow = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<ExecutionUserInputsDTO> currentFlowExecutionUserInputs = new SimpleObjectProperty<>();
}
