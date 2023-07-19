package com.main.stepper.client.resources.fxml.tabs.flowsexecution.tab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.data.URLManager;
import com.main.stepper.client.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.client.resources.fxml.reusable.executionelements.FlowExecutionElementsController;
import com.main.stepper.client.resources.fxml.reusable.flowinput.FlowInputController;
import com.main.stepper.client.resources.fxml.reusable.stepdetails.StepDetailsController;
import com.main.stepper.client.resources.fxml.tabs.flowsexecution.continuations.FlowContinuationsController;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.executionuserinputs.ExecutionUserInputsDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.main.stepper.client.resources.data.PropertiesManager.*;

public class FlowExecutionController {
    private Thread validateInputsThread = null;
    private List<FlowInputController> flowInputControllers;
    private List<Parent> flowInputComponents = new ArrayList<>();
    @FXML GridPane flowInputsGrid;
    @FXML FlowPane inputsFlowPane;
    @FXML Button startButton;
    @FXML CheckBox mandatoryBox;
    @FXML CheckBox optionalBox;
    @FXML FlowContinuationsController continuationsController;
    @FXML FlowExecutionElementsController executionElementsController;
    @FXML StepDetailsController stepExecutionDetailsController;

    public FlowExecutionController() {
    }

    @FXML public void initialize(){
        startButton.setOnAction(event -> startFlow());
        flowInputControllers = new ArrayList<>();
        flowInputComponents = new ArrayList<>();

        // add listener for selected flow change and update ui accordingly
        executionSelectedFlow.addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue == null)
                return;
            onCurrentFlowChange();
        });

        // setup sub-controllers
        executionElementsController.setBindings(executionRunningFlow, executionSelectedStep);
        stepExecutionDetailsController.setBinding(executionSelectedStep);
    }

    public void reset() {
        executionSelectedFlow.set(null);
        flowInputsGrid.setDisable(true);
        startButton.setDisable(true);
        optionalBox.setDisable(true);
        mandatoryBox.setDisable(true);
        if (validateInputsThread != null)
            validateInputsThread.interrupt();
        inputsFlowPane.getChildren().clear();

        flowInputControllers.clear();
        executionElementsController.reset();
        stepExecutionDetailsController.reset();
    }

    private void onCurrentFlowChange() {
        if (executionSelectedFlow.isNull().get()){
            validateInputsThread.interrupt();
            reset();
            return;
        }

        if (currentFlowExecutionUserInputs.isNull().not().getValue()) {
            synchronized (currentFlowExecutionUserInputs.get()) {
                if (validateInputsThread != null)
                    validateInputsThread.interrupt();
            }
        }
        else {
            if (validateInputsThread != null)
                validateInputsThread.interrupt();
        }
        requestExecutionUserInputs();


        executionElementsController.reset();
        stepExecutionDetailsController.reset();

        flowInputsGrid.setDisable(false);
        mandatoryBox.setDisable(false);
        optionalBox.setDisable(false);
    }

    // initialize input components - happens when currentFlowExecutionUserInputs is changed
    private void setInputs(FlowRunResultDTO runResult) {
        this.flowInputControllers.clear();
        this.flowInputComponents.clear();
        this.inputsFlowPane.getChildren().clear();
        List<FadeTransition> animations = new ArrayList<>();
        if (currentFlowExecutionUserInputs.isNotNull().get()) {
            synchronized (currentFlowExecutionUserInputs.get()) {
                for (DataIODTO dataIO : currentFlowExecutionUserInputs.get().getOpenUserInputs()) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(FlowInputController.class.getResource("FlowInput.fxml"));
                    try {
                        // load fxml file
                        Parent inputComp = loader.load();
                        FlowInputController flowInputController = loader.getController();
                        flowInputController.init(dataIO);

                        // get value for continuation if there is one
                        if (runResult != null) {
                            Map<DataIODTO, DataIODTO> customContinuationMappings = runResult.continuationMapping(executionSelectedFlow.get());
                            Object value = null;
                            DataIODTO key = customContinuationMappings.get(dataIO);
                            if (key != null)
                                value = runResult.flowExecutionContext().getVariable(key, key.type().getType());
                            if (value != null)
                                flowInputController.setValue(value.toString());
                        }

                        flowInputControllers.add(flowInputController);
                        flowInputComponents.add(inputComp);
                        inputComp.setOpacity(0.0);
                        inputsFlowPane.getChildren().add(inputComp);
                        // animate fade in
                        FadeTransition ft = new FadeTransition(Duration.millis(250), inputComp);
                        ft.setFromValue(0.0);
                        ft.setToValue(1.0);
                        animations.add(ft);
                    } catch (IOException ignored) {
                    }
                }
                for (int i = 0; i < animations.size() - 1; i++) {
                    final FadeTransition next = animations.get(i + 1);
                    animations.get(i).setOnFinished(event -> next.play());
                }
                animations.get(0).play();
            }
        }
    }

    @FXML private void startFlow() {
        stepExecutionDetailsController.reset();
        executionElementsController.reset();
        flowInputsGrid.setDisable(true);
        requestExecuteFlow();
    }

    @FXML private void toggleMandatory() {
        if(mandatoryBox.isSelected()){
            int startInd = 0;
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().necessity().equals(DataNecessity.MANDATORY)) {
                    inputsFlowPane.getChildren().add(startInd, flowInputComponents.get(i));
                    startInd++;
                }
            }
        }
        else {
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().necessity().equals(DataNecessity.MANDATORY)) {
                    inputsFlowPane.getChildren().remove(flowInputComponents.get(i));
                }
            }
        }
    }

    @FXML private void toggleOptional() {
        if(optionalBox.isSelected()){
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().necessity().equals(DataNecessity.OPTIONAL)) {
                    inputsFlowPane.getChildren().add(flowInputComponents.get(i));
                }
            }
        }
        else {
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().necessity().equals(DataNecessity.OPTIONAL)) {
                    inputsFlowPane.getChildren().remove(flowInputComponents.get(i));
                }
            }
        }
    }

    private void requestExecutionUserInputs() {
        if (executionSelectedFlow.isNull().not().getValue()) {
            HttpUrl.Builder urlBuilder = HttpUrl
                    .parse(URLManager.FLOW_EXECUTION)
                    .newBuilder();
            HttpUrl url = urlBuilder.addQueryParameter("flowName", executionSelectedFlow.get().name()).build();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = PropertiesManager.HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> new ErrorPopup("Can't reach server, try again later."));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        final ExecutionUserInputsDTO userInputs = gson.fromJson(response.body().string(), ExecutionUserInputsDTO.class);
                        Platform.runLater(() -> {
                            currentFlowExecutionUserInputs.set(userInputs);
                            setInputs(continuation.get());
                            continuation.set(null);
                            initializeValidationThread();
                        });
                    }
                    else {
                        Platform.runLater(() -> new ErrorPopup("Inconsistent data, please wait a few seconds for a system refresh."));
                    }
                }
            });
        }
    }

    private void requestExecuteFlow() {
        if (currentFlowExecutionUserInputs.isNotNull().get() && executionSelectedFlow.isNotNull().get()) {
            HttpUrl.Builder urlBuilder = HttpUrl
                    .parse(URLManager.FLOW_EXECUTION)
                    .newBuilder();
            HttpUrl url = urlBuilder.addQueryParameter("flowName", executionSelectedFlow.get().name()).build();
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            RequestBody body = RequestBody.create(gson.toJson(currentFlowExecutionUserInputs.get()), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = PropertiesManager.HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> new ErrorPopup("Can't reach server, try again later."));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        UUID executionId = gson.fromJson(response.body().string(), UUID.class);
                        Platform.runLater(() -> {
                            currentRunningFlowUUID.set(executionId);
                        });
                    }
                    else {
                        Platform.runLater(() -> new ErrorPopup("Inconsistent data, please wait a few seconds for a system refresh."));
                    }
                }
            });
        }
    }

    private void initializeValidationThread() {
        // initialize validation thread
        validateInputsThread = new Thread(()->{
            while (true){
                try{
                    if (currentFlowExecutionUserInputs.isNull().getValue())
                        return;
                    synchronized (currentFlowExecutionUserInputs.get()) {
                        synchronized (flowInputControllers){
                            for(FlowInputController flowInputController : flowInputControllers) {
                                try {
                                    if(flowInputController.getValue().isEmpty()){
                                        currentFlowExecutionUserInputs.get().readUserInput(flowInputController.input(), "");
                                        Platform.runLater(() -> flowInputController.setValid(false));
                                    }

                                    currentFlowExecutionUserInputs.get().readUserInput(flowInputController.input(), flowInputController.getValue());
                                    // input is valid: green
                                    Platform.runLater(() -> {
                                        flowInputController.setInputStyle(
                                                "-fx-text-box-border: lightgreen ;\n" +
                                                        "  -fx-focus-color: lightgreen ;"
                                        );
                                        flowInputController.setValid(true);
                                    });
                                } catch (BadTypeException e) {
                                    if(flowInputController.input().necessity().equals(DataNecessity.OPTIONAL)){
                                        // input is optional and bad type: yellow
                                        Platform.runLater(() -> {
                                            flowInputController.setInputStyle(
                                                    "-fx-text-box-border: yellow ;\n" +
                                                            "  -fx-focus-color: yellow ;"
                                            );
                                            flowInputController.setValid(true);
                                        });
                                    }
                                    else{
                                        // input is mandatory and bad type: red
                                        Platform.runLater(() -> {
                                            flowInputController.setInputStyle(
                                                    "-fx-text-box-border: red ;\n" +
                                                            "  -fx-focus-color: red ;"
                                            );
                                            flowInputController.setValid(false);
                                        });
                                    }
                                }
                            }
                        }

                        if (currentFlowExecutionUserInputs.get().validateUserInputs())
                            Platform.runLater(() -> startButton.setDisable(false));
                        else
                            Platform.runLater(() -> startButton.setDisable(true));
                    }
                    Thread.sleep(150);
                }catch (InterruptedException e) {
                    return;
                }
            }
        });
        validateInputsThread.setName("Validate Inputs Thread");
        validateInputsThread.setDaemon(true);
        validateInputsThread.start();
    }
}
