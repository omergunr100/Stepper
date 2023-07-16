package com.main.stepper.admin.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import com.main.stepper.shared.structures.users.UserData;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        // update flowInformationList
        updateFlowInformation();
    }

    @Override
    public void run() {
        // todo: for debugging only, remove from code before release
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        while (true) {
            try {
                checkHealth();
                sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static void checkHealth() {
        Request request = new Request.Builder()
                .url(URLManager.SERVER_HEALTH)
                .addHeader("isAdmin", "true")
                .get()
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    health.set(false);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() == 200) {
                        health.set(true);
                        updateProperties();
                    }
                    response.close();
                }
            });
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
                        List<FlowRunResultDTO> flowRunResultList = gson.fromJson(response.body().string(), new TypeToken<ArrayList<FlowRunResultDTO>>(){}.getType());
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
        List<StepRunResultDTO> stepRunResultList = new ArrayList<>();
        synchronized (stepRunResults) {
            for (FlowRunResultDTO result : flowRunResults) {
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
                        List<UserData> dataList = gson.fromJson(response.body().string(), new TypeToken<ArrayList<UserData>>() {}.getType());
                        Platform.runLater(() -> {
                            synchronized (userDataList) {
                                for (UserData data : dataList) {
                                    Optional<UserData> first = userDataList.stream().filter(user -> user.name().equals(data.name())).findFirst();
                                    if (first.isPresent()) {
                                        if (!first.get().equals(data))
                                            first.get().update(data);
                                    }
                                    else {
                                        userDataList.add(data);
                                    }
                                }
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
                        List<Role> roles = gson.fromJson(response.body().string(), new TypeToken<ArrayList<Role>>() {}.getType());
                        Platform.runLater(() -> {
                            synchronized (rolesList) {
                                List<Role> finalList = new ArrayList<>();
                                finalList.addAll(roles);
                                if (localRole.isNotNull().get())
                                    finalList.add(localRole.get());

                                List<Role> toAdd = new ArrayList<>();
                                for (Role role : finalList) {
                                    // check if there was a role by the same name
                                    Optional<Role> first = rolesList.stream().filter(curr -> curr.name().equals(role.name())).findFirst();
                                    if (first.isPresent()) {
                                        // check if exact match
                                        Role found = first.get();
                                        // if not exact match update
                                        if (!found.equals(role))
                                            found.update(role);
                                    }
                                    else {
                                        // no match - add to list
                                        toAdd.add(role);
                                    }
                                }

                                List<Role> toRemove = new ArrayList<>();
                                for (Role role : rolesList) {
                                    // check if a role of the same name doesn't exist in new list
                                    Optional<Role> match = finalList.stream().filter(curr -> curr.name().equals(role.name())).findFirst();
                                    if (!match.isPresent())
                                        toRemove.add(role);
                                }

                                rolesList.removeAll(toRemove);
                                rolesList.addAll(toAdd);
                            }
                        });
                    }
                }
            });
        }
    }

    private static void updateFlowInformation() {
        Request request = new Request.Builder()
                .addHeader("isAdmin", "true")
                .url(URLManager.FLOW_INFORMATION)
                .post(RequestBody.create(null, new byte[0]))
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
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.enableComplexMapKeySerialization().create();
                        String responseBody = response.body().string();
                        final List<FlowInfoDTO> informations = gson.fromJson(responseBody, new TypeToken<ArrayList<FlowInfoDTO>>(){}.getType());
                        Platform.runLater(() -> {
                            synchronized (flowInformationList) {
                                // if list was empty, add all
                                if (flowInformationList.isEmpty())
                                    flowInformationList.addAll(informations);
                                else {
                                    // else add new information
                                    List<FlowInfoDTO> newInfoList = informations.stream()
                                            .filter(information -> !flowInformationList.contains(information))
                                            .collect(Collectors.toList());
                                    List<FlowInfoDTO> deletedInfoList = flowInformationList.stream()
                                            .filter(information -> !informations.contains(information))
                                            .collect(Collectors.toList());
                                    flowInformationList.addAll(newInfoList);
                                    flowInformationList.removeAll(deletedInfoList);
                                }
                            }
                        });
                    }
                    else
                        response.close();
                }
            });
        }
    }
}
