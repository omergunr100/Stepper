package com.main.stepper.admin.resources.data;

import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import com.main.stepper.shared.structures.users.UserData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;

public class PropertiesManager {
    // HTTP client
    public static OkHttpClient HTTP_CLIENT = new OkHttpClient();

    // JavaFX properties
    public static SimpleObjectProperty<Stage> primaryStage = new SimpleObjectProperty<>();

    // General use properties updated from server
    public static SimpleBooleanProperty health = new SimpleBooleanProperty(true);
    public static SimpleStringProperty lastLoadedFile = new SimpleStringProperty("");
    public static ObservableList<Role> rolesList = FXCollections.observableArrayList();
    public static ObservableList<UserData> userDataList = FXCollections.observableArrayList();
    public static ObservableList<FlowInfoDTO> flowInformationList = FXCollections.observableArrayList();
    public static ObservableList<FlowRunResultDTO> flowRunResults = FXCollections.observableArrayList();
    public static ObservableList<StepRunResultDTO> stepRunResults = FXCollections.observableArrayList();

    // user management screen properties
    public static SimpleObjectProperty<UserData> selectedUser = new SimpleObjectProperty<>();

    // execution history screen properties
    public static SimpleObjectProperty<FlowRunResultDTO> executionHistorySelectedFlow = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<StepRunResultDTO> executionHistorySelectedStep = new SimpleObjectProperty<>();

    // flow selection properties
    public static SimpleObjectProperty<FlowInfoDTO> selectedFlow = new SimpleObjectProperty<>();
}
