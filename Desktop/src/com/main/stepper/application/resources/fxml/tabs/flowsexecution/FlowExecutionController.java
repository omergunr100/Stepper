package com.main.stepper.application.resources.fxml.tabs.flowsexecution;

import com.main.stepper.application.resources.fxml.reusable.flowdetails.FlowTreeViewController;
import com.main.stepper.application.resources.fxml.reusable.flowinput.FlowInputController;
import com.main.stepper.application.resources.fxml.root.RootController;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;

public class FlowExecutionController {
    private RootController rootController;
    private IFlowDefinition currentFlow = null;
    private volatile ExecutionUserInputs executionUserInputs = null;
    private Thread validateInputsThread = null;
    private List<FlowInputController> flowInputControllers;
    @FXML SplitPane root;
    @FXML FlowPane inputsFlowPane;
    @FXML Button startButton;
    @FXML FlowTreeViewController flowDetailsTreeController;

    public FlowExecutionController() {
    }

    @FXML public void initialize(){
        startButton.setDisable(true);
        startButton.setOnAction(event -> startFlow());
        flowInputControllers = new ArrayList<>();
    }

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    public void setCurrentFlow(IFlowDefinition currentFlow) {
        this.currentFlow = currentFlow;
        flowDetailsTreeController.setCurrentFlow(currentFlow.information());
        this.executionUserInputs = rootController.getEngine().getExecutionUserInputs(currentFlow.name());
        // initialize validation thread
        if(validateInputsThread != null)
            validateInputsThread.interrupt();
        validateInputsThread = new Thread(()->{
            while (true){
                // todo: add read from input fields to executionUserInputs
                synchronized (executionUserInputs) {
                    if (executionUserInputs.validateUserInputs())
                        startButton.setDisable(false);
                    else
                        startButton.setDisable(true);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        });
        validateInputsThread.setDaemon(true);


        setInputs();
    }

    private void setInputs() {
        // todo: add inputs to flowInputControllers
        inputsFlowPane.getChildren().clear();
        currentFlow.information().openUserInputs().forEach(input -> {
            input.getName();
            input.getUserString();
            input.getNecessity();
            input.getDataDefinition();
        });
    }

    public void validateInputs() {

    }

    private void startFlow() {
        rootController.getEngine().runFlow(currentFlow.name(), executionUserInputs);
        executionUserInputs = rootController.getEngine().getExecutionUserInputs(currentFlow.name());
        // todo: empty inputs
    }
}
