package com.main.stepper.client.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.client.resources.data.URLManager;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.shared.structures.chat.message.Message;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.main.stepper.client.resources.data.PropertiesManager.*;

public class UpdatePropertiesThread extends Thread{
    private static void updateProperties() {
        // update userData
        updateUserData();
        // update flowRunResults
        updateFlowRunResults();
        // update stepRunResults
        updateStepRunResults();
        // update available flow information list
        updateFlowInformation();
        // update currently running flow
        updateCurrentlyRunningFlow();
        // update usernames list
        updateUsernamesList();
        // update chat messages
        updateChatMessages();
    }

    @Override
    public void run() {
        while (true) {
            try {
                checkServerHealth();
                sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static void checkServerHealth() {
        Request request = new Request.Builder()
                .url(URLManager.SERVER_HEALTH)
                .get()
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // freeze application
                    Platform.runLater(() -> health.set(false));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.code() == 200) {
                        Platform.runLater(() -> health.set(true));
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
                        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                        List<FlowRunResultDTO> temp = gson.fromJson(response.body().string(), new TypeToken<ArrayList<FlowRunResultDTO>>(){}.getType());
                        final List<FlowRunResultDTO> flowRunResultList = temp.stream().map(FlowRunResultDTO::fix).collect(Collectors.toList());
                        Platform.runLater(() -> {
                            synchronized (flowRunResults) {
                                // check if not manager and try to delete all non-user flows
                                if (!isManager.get() && !flowRunResults.isEmpty()) {
                                    List<FlowRunResultDTO> toRemove = flowRunResults.stream()
                                            .filter(result -> !result.user().equals(userName.get()))
                                            .collect(Collectors.toList());
                                    flowRunResults.removeAll(toRemove);
                                }
                                // if received an empty list exit
                                if (flowRunResultList.isEmpty())
                                    return;
                                // if list was empty, add all
                                if (flowRunResults.isEmpty())
                                    flowRunResults.addAll(flowRunResultList);
                                else {
                                    // check for new run results and add them
                                    int i = 0;
                                    while (!flowRunResultList.get(i).runId().equals(flowRunResults.get(i).runId())) {
                                        flowRunResults.add(i, flowRunResultList.get(i));
                                        i++;
                                    }
                                    // for the rest check for status change and if so update
                                    for (; i < flowRunResultList.size() && i < flowRunResults.size(); i++) {
                                        if (!flowRunResultList.get(i).equals(flowRunResults.get(i))) {
                                            flowRunResults.set(i, flowRunResultList.get(i));
                                        }
                                    }
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

    private static void updateStepRunResults() {
        // create a list of all stepRunResults from flowRunResults
        List<StepRunResultDTO> stepRunResultList = new ArrayList<>();
        synchronized (stepRunResults) {
            for (FlowRunResultDTO result : flowRunResults) {
                stepRunResultList.addAll(result.stepRunResults().stream().map(StepRunResultDTO::fix).collect(Collectors.toList()));
            }
        }
        if (stepRunResultList.isEmpty())
            return;
        Platform.runLater(() -> stepRunResults.setAll(stepRunResultList));
    }

    private static void updateUserData() {
        Request request = new Request.Builder()
                .url(URLManager.USERS_DATA)
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
                        UserData newUserData = gson.fromJson(response.body().string(), UserData.class);
                        Platform.runLater(() -> {
                            if (!userName.equals(newUserData.name()))
                                userName.set(newUserData.name());
                            if (!isManager.equals(newUserData.isManager()))
                                isManager.set(newUserData.isManager());
                            if (!roles.equals(newUserData.roles()))
                                roles.setAll(newUserData.roles());
                            if (!flowExecutionHistory.equals(newUserData.flowExecutionHistory()))
                                flowExecutionHistory.setAll(newUserData.flowExecutionHistory());
                        });
                    }
                    else
                        response.close();
                }
            });
        }
    }

    private static void updateFlowInformation() {
        Set<String> flows = new HashSet<>();
        if (!isManager.get()) {
            synchronized (roles) {
                if (roles.isEmpty())
                    return;
                for (Role role : roles) {
                    flows.addAll(role.allowedFlows());
                }
            }
            if (flows.isEmpty())
                return;
        }

        Gson gson = new Gson();
        RequestBody body = RequestBody.create(gson.toJson(flows, new TypeToken<HashSet<String>>() {}.getType()), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(URLManager.FLOW_INFORMATION)
                .post(body)
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

    private static void updateCurrentlyRunningFlow() {
        if (currentRunningFlowUUID.isNotNull().get() && !flowRunResults.isEmpty()) {
            synchronized (flowRunResults) {
                Platform.runLater(() -> {
                    flowRunResults.stream()
                            .filter(exe -> exe.runId().equals(currentRunningFlowUUID.get()))
                            .findFirst()
                            .ifPresent(exe -> {
                                executionRunningFlow.set(exe);
                                if(!exe.result().equals(FlowResult.RUNNING))
                                    currentRunningFlowUUID.set(null);
                            });
                });
            }
        }
    }

    private static void updateUsernamesList() {
        Request request = new Request.Builder()
                .url(URLManager.USERS_NAMES)
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
                        List<String> newUserDataList = gson.fromJson(response.body().string(), new TypeToken<ArrayList<String>>(){}.getType());
                        Platform.runLater(() -> {
                            List<String> toAdd = newUserDataList.stream().filter(name -> !usernamesList.contains(name)).collect(Collectors.toList());
                            List<String> toRemove = usernamesList.stream().filter(name -> !newUserDataList.contains(name)).collect(Collectors.toList());
                            usernamesList.removeAll(toRemove);
                            usernamesList.addAll(toAdd);
                        });
                    }
                    else
                        response.close();
                }
            });
        }
    }

    private static void updateChatMessages() {
        Request request = new Request.Builder()
                .url(URLManager.CHAT)
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
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        List<Message> messages = gson.fromJson(response.body().string(), new TypeToken<ArrayList<Message>>() {}.getType());
                        Platform.runLater(() -> {
                            synchronized (chatMessages) {
                                // if list was empty, add all
                                if (chatMessages.isEmpty())
                                    chatMessages.addAll(messages);
                                else {
                                    // else add new messages
                                    List<Message> newMessages = messages.stream()
                                            .filter(message -> !chatMessages.contains(message))
                                            .collect(Collectors.toList());
                                    chatMessages.addAll(newMessages);
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