package com.main.stepper.admin.application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.users.UserData;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class UpdatePropertiesThread extends Thread{
    private static void updateProperties() {
        // update flowRunResults
        updateFlowRunResults();
        // update stepRunResults
        updateStepRunResults();
        // update userDataList
        updateUserDataList();
        // update rolesList
        updateRolesList();
    }

    @Override
    public void run() {
        while (true) {
            try {
                updateProperties();
                sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static void updateFlowRunResults() {
        Request request = new Request.Builder()
                .url(URLManager.FLOW_RUN_RESULTS)
                .addHeader("isAdmin", "true")
                .get()
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // ignore failure, updates regularly
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.code() == 200) {
                        Gson gson = new Gson();
                        List<IFlowRunResult> flowRunResultList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<IFlowRunResult>>(){}.getType());
                        Platform.runLater(() -> {
                            synchronized (flowRunResults) {
                                // if received an empty list exit
                                if (flowRunResultList.isEmpty())
                                    return;
                                // if list was empty, add all
                                if (flowRunResults.isEmpty())
                                    flowRunResults.addAll(flowRunResultList);
                                else {
                                    // if list wasn't empty only head of list is new relevant data, add it to the head
                                    int i = 0;
                                    while (!flowRunResultList.get(i).equals(flowRunResults.get(i))) {
                                        flowRunResults.add(i, flowRunResultList.get(i));
                                        i++;
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private static void updateStepRunResults() {
        // create a list of all stepRunResults from flowRunResults
        List<IStepRunResult> stepRunResultList = new ArrayList<>();
        synchronized (stepRunResults) {
            for (IFlowRunResult result : flowRunResults) {
                stepRunResultList.addAll(result.stepRunResults());
            }
        }
        if (stepRunResultList.isEmpty())
            return;
        stepRunResults.setAll(stepRunResultList);
    }

    private static void updateUserDataList() {
        Request request = new Request.Builder()
                .url(URLManager.USERS_DATA)
                .addHeader("isAdmin", "true")
                .get()
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // ignore failure, updates regularly
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        List<UserData> dataList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<UserData>>() {}.getType());
                        Platform.runLater(() -> {
                            synchronized (userDataList) {
                                // if received an empty list exit
                                if (dataList.isEmpty())
                                    return;
                                // else set all
                                userDataList.setAll(dataList);
                            }
                        });
                    }
                }
            });
        }
    }

    private static void updateRolesList() {
        Request request = new Request.Builder()
                .url(URLManager.ROLES_MANAGEMENT)
                .addHeader("isAdmin", "true")
                .get()
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // ignore failure, updates regularly
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        List<Role> roles = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Role>>() {}.getType());
                        Platform.runLater(() -> {
                            synchronized (rolesList) {
                                // if received an empty list exit
                                if (roles.isEmpty())
                                    return;
                                // else set all
                                rolesList.setAll(roles);
                            }
                        });
                    }
                }
            });
        }
    }
}
