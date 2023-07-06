package com.main.stepper.admin.resources.data;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.users.UserData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okhttp3.OkHttpClient;

public class PropertiesManager {
    public static OkHttpClient HTTP_CLIENT = new OkHttpClient();

    public static ObservableList<IFlowRunResult> flowRunResults = FXCollections.observableArrayList();
    public static ObservableList<IStepRunResult> stepRunResults = FXCollections.observableArrayList();

    public static ObservableList<UserData> userDataList = FXCollections.observableArrayList();
    public static ObservableList<Role> rolesList = FXCollections.observableArrayList();
}
