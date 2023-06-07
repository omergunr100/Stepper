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
    @FXML FlowTreeViewController flowDetailsTreeController;
    @FXML VBox executionDetailsVBoxController;
    @FXML CheckBox mandatoryBox;
    @FXML CheckBox optionalBox;

    public FlowExecutionController() {
    }

    @FXML public void initialize(){
        startButton.setOnAction(event -> startFlow());
        flowInputControllers = new ArrayList<>();
        flowInputComponents = new ArrayList<>();
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
        flowDetailsTreeController.setCurrentFlow(null);
    }

    public void setCurrentFlow(IFlowDefinition currentFlow) {
        this.currentFlow = currentFlow;
        flowDetailsTreeController.setCurrentFlow(currentFlow.information());
        executionUserInputs = rootController.getEngine().getExecutionUserInputs(currentFlow.name());

        setInputs();

        // initialize validation thread
        if(validateInputsThread != null)
            validateInputsThread.interrupt();
        validateInputsThread = new Thread(()->{
            while (true){
                try{
                    synchronized (executionUserInputs) {
                        synchronized (flowInputControllers){
                            for(FlowInputController flowInputController : flowInputControllers) {
                                try {
                                    if(flowInputController.getValue().isEmpty()){
                                        executionUserInputs.readUserInput(flowInputController.input(), null);
                                        flowInputController.setValid(false);
                                        throw new BadTypeException("Empty input");
                                    }

                                    executionUserInputs.readUserInput(flowInputController.input(), flowInputController.getValue());
                                    // input is valid: green
                                    flowInputController.setInputStyle(
                                            "-fx-text-box-border: lightgreen ;\n" +
                                            "  -fx-focus-color: lightgreen ;"
                                    );
                                    flowInputController.setValid(true);
                                } catch (BadTypeException e) {
                                    if(flowInputController.input().getNecessity().equals(DataNecessity.OPTIONAL)){
                                        // input is optional and bad type: yellow
                                        flowInputController.setInputStyle(
                                                "-fx-text-box-border: yellow ;\n" +
                                                "  -fx-focus-color: yellow ;"
                                        );
                                        flowInputController.setValid(true);
                                    }
                                    else{
                                        // input is mandatory and bad type: red
                                        flowInputController.setInputStyle(
                                                "-fx-text-box-border: red ;\n" +
                                                "  -fx-focus-color: red ;"
                                        );
                                        flowInputController.setValid(false);
                                    }
                                }
                            }
                        }

                        if (executionUserInputs.validateUserInputs())
                            startButton.setDisable(false);
                        else
                            startButton.setDisable(true);
                    }
                    Thread.sleep(150);
                }catch (InterruptedException e) {
                    break;
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
    private void setInputs() {
        this.flowInputControllers.clear();
        this.flowInputComponents.clear();
        this.inputsFlowPane.getChildren().clear();
        List<FadeTransition> animations = new ArrayList<>();
        for(IDataIO dataIO : executionUserInputs.getOpenUserInputs()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FlowInputController.class.getResource("FlowInput.fxml"));
            try {
                // load fxml file
                Parent inputComp = loader.load();
                FlowInputController flowInputController = loader.getController();
                flowInputController.init(dataIO);
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
        for(int i = 0; i < animations.size() - 1; i++){
            final FadeTransition next = animations.get(i + 1);
            animations.get(i).setOnFinished(event -> next.play());
        }
        animations.get(0).play();
    }

    @FXML private void startFlow() {
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

    private void addExecutionData(Parent component) {

    }
}
