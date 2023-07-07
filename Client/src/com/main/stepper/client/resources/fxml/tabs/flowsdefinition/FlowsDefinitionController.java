package com.main.stepper.client.resources.fxml.tabs.flowsdefinition;

import com.google.gson.Gson;
import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.data.URLManager;
import com.main.stepper.client.resources.fxml.reusable.flowdetails.FlowTreeViewController;
import com.main.stepper.shared.structures.executionuserinputs.ExecutionUserInputsDTO;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FlowsDefinitionController {
    @FXML private TableView<FlowInfoDTO> flowsTableView;
    @FXML private FlowTreeViewController selectedFlowTreeController;
    @FXML private Button executeFlowButton;

    public FlowsDefinitionController() {
    }

    @FXML public void initialize(){
        // Initialize flow table
        TableColumn<FlowInfoDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));
        nameColumn.setPrefWidth(100);


        TableColumn<FlowInfoDTO, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().description()));
        descriptionColumn.setPrefWidth(200);

        TableColumn<FlowInfoDTO, String> numberOfStepsColumn = new TableColumn<>("Number of steps");
        numberOfStepsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().steps().size())));
        numberOfStepsColumn.setPrefWidth(150);

        TableColumn<FlowInfoDTO, String> numberOfFreeInputsColumn = new TableColumn<>("Number of free inputs");
        numberOfFreeInputsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().openUserInputs().size())));
        numberOfFreeInputsColumn.setPrefWidth(150);

        TableColumn<FlowInfoDTO, String> numberOfContinuationsColumn = new TableColumn<>("Number of continuations");
        numberOfContinuationsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().continuations().size())));
        numberOfContinuationsColumn.setPrefWidth(150);

        flowsTableView.getColumns().addAll(nameColumn, descriptionColumn, numberOfStepsColumn, numberOfFreeInputsColumn, numberOfContinuationsColumn);

        flowsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            PropertiesManager.currentFlow.set(newValue);
            this.executeFlowButton.setDisable(newValue == null);
        });

        flowsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // bind to property
        PropertiesManager.flowInformationList.addListener((ListChangeListener<FlowInfoDTO>) c -> {
            c.next();
            c.getRemoved().forEach(flowInformation -> {
                if (flowInformation.equals(PropertiesManager.currentFlow.get()))
                    PropertiesManager.currentFlow.set(null);
                flowsTableView.getItems().remove(flowInformation);
            });
            flowsTableView.getItems().addAll(c.getAddedSubList());
        });
    }

    public void executeFlow() {
        if (PropertiesManager.currentFlow.get() != null) {
            HttpUrl.Builder urlBuilder = HttpUrl
                    .parse(URLManager.FLOW_EXECUTION)
                    .newBuilder();
            HttpUrl url = urlBuilder.addQueryParameter("flowName", PropertiesManager.currentFlow.getName()).build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = PropertiesManager.HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // todo: decide whether to show error message to the user (can't reach server) or just ignore
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        ExecutionUserInputsDTO userInputs = gson.fromJson(response.body().string(), ExecutionUserInputsDTO.class);
                        PropertiesManager.currentFlowExecutionUserInputs.set(userInputs);
                    }
                    else {
                        // todo: show user a message asking to wait for refresh because maybe roles were changed or manager status revoked
                    }
                }
            });
        }
    }
}
