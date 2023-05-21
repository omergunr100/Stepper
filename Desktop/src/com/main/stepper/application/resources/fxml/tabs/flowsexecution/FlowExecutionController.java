package com.main.stepper.application.resources.fxml.tabs.flowsexecution;

import com.main.stepper.application.resources.fxml.reusable.flowdetails.FlowTreeViewController;
import com.main.stepper.application.resources.fxml.reusable.flowinput.FlowInputController;
import com.main.stepper.application.resources.fxml.root.RootController;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import java.io.IOException;
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
    @FXML volatile Button startButton;
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

        setInputs();

        // initialize validation thread
        if(validateInputsThread != null)
            validateInputsThread.interrupt();
        // todo: add listener for tab change, only run this thread if this tab is selected
        validateInputsThread = new Thread(()->{
            while (true){
                synchronized (executionUserInputs) {
                    synchronized (flowInputControllers){
                        for(FlowInputController flowInputController : flowInputControllers) {
                            try {
                                if(flowInputController.getValue().isEmpty()){
                                    executionUserInputs.readUserInput(flowInputController.input(), null);
                                    throw new BadTypeException("Empty input");
                                }

                                executionUserInputs.readUserInput(flowInputController.input(), flowInputController.getValue());
                                // input is valid: green
                                flowInputController.setInputStyle(
                                        "-fx-text-box-border: lightgreen ;\n" +
                                        "  -fx-focus-color: lightgreen ;"
                                );
                            } catch (BadTypeException e) {
                                if(flowInputController.input().getNecessity().equals(DataNecessity.OPTIONAL)){
                                    // input is optional and bad type: yellow
                                    flowInputController.setInputStyle(
                                            "-fx-text-box-border: yellow ;\n" +
                                            "  -fx-focus-color: yellow ;"
                                    );
                                }
                                else{
                                    // input is mandatory and bad type: red
                                    flowInputController.setInputStyle(
                                            "-fx-text-box-border: red ;\n" +
                                            "  -fx-focus-color: red ;"
                                    );
                                }
                            }
                        }
                    }

                    if (executionUserInputs.validateUserInputs())
                        startButton.setDisable(false);
                    else
                        startButton.setDisable(true);
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                    return;
                }
            }
        });
        validateInputsThread.setDaemon(true);
         validateInputsThread.start();

    }

    // initialize input components
    private void setInputs() {
        this.flowInputControllers.clear();
        this.inputsFlowPane.getChildren().clear();
        List<FadeTransition> animations = new ArrayList<>();
        for(IDataIO dataIO : executionUserInputs.getOpenUserInputs()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FlowInputController.class.getResource("FlowInput.fxml"));
            try {
                Parent inputComp = loader.load();
                FlowInputController flowInputController = loader.getController();
                flowInputController.init(dataIO);
                flowInputControllers.add(flowInputController);
                inputComp.setOpacity(0.0);
                inputsFlowPane.getChildren().add(inputComp);
                // animate fade in
                FadeTransition ft = new FadeTransition(Duration.millis(500), inputComp);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                animations.add(ft);
            } catch (IOException ignored) {
            }
        }
        for(int i = 0; i < animations.size() - 1; i++){
            final FadeTransition next = animations.get(i + 1);
            animations.get(i).setOnFinished(event -> next.play());
        }
        animations.get(0).play();
    }

    public void validateInputs() {

    }

    private void startFlow() {
        rootController.getEngine().runFlow(currentFlow.name(), executionUserInputs);
        executionUserInputs = rootController.getEngine().getExecutionUserInputs(currentFlow.name());
        // todo: empty inputs
    }
}