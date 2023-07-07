package com.main.stepper.client.resources.fxml.tabs.flowsexecution.tab;

import com.main.stepper.application.resources.fxml.reusable.flowdetails.FlowTreeViewController;
import com.main.stepper.application.resources.fxml.reusable.flowinput.FlowInputController;
import com.main.stepper.application.resources.fxml.root.RootController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.continuations.FlowContinuationsController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements.FlowExecutionElementsController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.stepdetails.StepDetailsController;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowExecutionController {
    private RootController rootController;
    private IFlowDefinition currentFlow = null;
    private volatile ExecutionUserInputs executionUserInputs = null;
    private Thread validateInputsThread = null;
    private List<FlowInputController> flowInputControllers;
    private List<Parent> flowInputComponents = new ArrayList<>();
    @FXML SplitPane root;
    @FXML FlowPane inputsFlowPane;
    @FXML Button startButton;
    @FXML CheckBox mandatoryBox;
    @FXML CheckBox optionalBox;
    @FXML FlowContinuationsController continuationsController;
    @FXML FlowExecutionElementsController executionElementsController;
    @FXML StepDetailsController stepDetailsController;

    public FlowExecutionController() {
    }

    @FXML public void initialize(){
        startButton.setOnAction(event -> startFlow());
        flowInputControllers = new ArrayList<>();
        flowInputComponents = new ArrayList<>();

        continuationsController.setParent(this);
        executionElementsController.setFlowExecutionController(this);
        executionElementsController.autoUpdate();
    }

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    public void reset() {
        startButton.setDisable(true);
        optionalBox.setDisable(true);
        mandatoryBox.setDisable(true);
        currentFlow = null;
        if (validateInputsThread != null)
            validateInputsThread.interrupt();
        executionUserInputs = null;
        inputsFlowPane.getChildren().clear();

        flowInputControllers.clear();
        executionElementsController.reset();
        continuationsController.reset();
        stepDetailsController.reset();
    }

    public void setCurrentFlow(IFlowDefinition currentFlow, IFlowRunResult context) {
        this.currentFlow = currentFlow;
        if (currentFlow == null){
            validateInputsThread.interrupt();
            reset();
            return;
        }

        if (executionUserInputs != null) {
            synchronized (executionUserInputs) {
                executionUserInputs = rootController.getEngine().getExecutionUserInputs(currentFlow.name());
                if (validateInputsThread != null)
                    validateInputsThread.interrupt();
            }
        }
        else {
            executionUserInputs = rootController.getEngine().getExecutionUserInputs(currentFlow.name());
            if (validateInputsThread != null)
                validateInputsThread.interrupt();
        }
        executionElementsController.reset();
        continuationsController.reset();
        stepDetailsController.reset();

        setInputs(context);

        // initialize validation thread
        validateInputsThread = new Thread(()->{
            while (true){
                try{
                    if (executionUserInputs == null)
                        return;
                    synchronized (executionUserInputs) {
                        synchronized (flowInputControllers){
                            for(FlowInputController flowInputController : flowInputControllers) {
                                try {
                                    if(flowInputController.getValue().isEmpty()){
                                        executionUserInputs.readUserInput(flowInputController.input(), "");
                                        Platform.runLater(() -> flowInputController.setValid(false));
                                    }

                                    executionUserInputs.readUserInput(flowInputController.input(), flowInputController.getValue());
                                    // input is valid: green
                                    Platform.runLater(() -> {
                                        flowInputController.setInputStyle(
                                                "-fx-text-box-border: lightgreen ;\n" +
                                                "  -fx-focus-color: lightgreen ;"
                                        );
                                        flowInputController.setValid(true);
                                    });
                                } catch (BadTypeException e) {
                                    if(flowInputController.input().getNecessity().equals(DataNecessity.OPTIONAL)){
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

                        if (executionUserInputs.validateUserInputs())
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

        mandatoryBox.setDisable(false);
        optionalBox.setDisable(false);
    }

    // initialize input components
    private void setInputs(IFlowRunResult runResult) {
        this.flowInputControllers.clear();
        this.flowInputComponents.clear();
        this.inputsFlowPane.getChildren().clear();
        List<FadeTransition> animations = new ArrayList<>();
        synchronized (executionUserInputs) {
            for (IDataIO dataIO : executionUserInputs.getOpenUserInputs()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(FlowInputController.class.getResource("FlowInput.fxml"));
                try {
                    // load fxml file
                    Parent inputComp = loader.load();
                    FlowInputController flowInputController = loader.getController();
                    flowInputController.init(dataIO);

                    // get value for continuation if there is one
                    if (runResult != null) {
                        Map<IDataIO, IDataIO> customContinuationMappings = runResult.flowDefinition().continuationMapping(currentFlow);
                        Object value = null;
                        IDataIO key = customContinuationMappings.get(dataIO);
                        if (key != null)
                            value = runResult.flowExecutionContext().getVariable(key, key.getDataDefinition().getType());
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

    @FXML private void startFlow() {
        continuationsController.reset();
        stepDetailsController.reset();
        executionElementsController.reset();

        rootController.getEngine().runFlow(currentFlow.name(), executionUserInputs);
        executionUserInputs = rootController.getEngine().getExecutionUserInputs(currentFlow.name());
    }

    @FXML private void toggleMandatory() {
        if(mandatoryBox.isSelected()){
            int startInd = 0;
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().getNecessity().equals(DataNecessity.MANDATORY)) {
                    inputsFlowPane.getChildren().add(startInd, flowInputComponents.get(i));
                    startInd++;
                }
            }
        }
        else {
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().getNecessity().equals(DataNecessity.MANDATORY)) {
                    inputsFlowPane.getChildren().remove(flowInputComponents.get(i));
                }
            }
        }
    }

    @FXML private void toggleOptional() {
        if(optionalBox.isSelected()){
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().getNecessity().equals(DataNecessity.OPTIONAL)) {
                    inputsFlowPane.getChildren().add(flowInputComponents.get(i));
                }
            }
        }
        else {
            for (int i = 0; i < flowInputControllers.size(); i++) {
                if (flowInputControllers.get(i).input().getNecessity().equals(DataNecessity.OPTIONAL)) {
                    inputsFlowPane.getChildren().remove(flowInputComponents.get(i));
                }
            }
        }
    }

    public void updateContinuations() {
        if (currentFlow != null)
            continuationsController.setContinuations(currentFlow.continuations());
    }

    public void selectStepForDetails(IStepRunResult stepResult) {
        stepDetailsController.setStep(stepResult);
    }
}
