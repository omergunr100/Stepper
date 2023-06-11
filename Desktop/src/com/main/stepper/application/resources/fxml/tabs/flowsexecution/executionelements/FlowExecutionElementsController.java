package com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements;

import com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements.element.ElementController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.tab.FlowExecutionController;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import com.main.stepper.flow.definition.api.FlowResult;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlowExecutionElementsController {
    private Property<IFlowRunResult> currentFlowResult;
    private Property<IStepRunResult> currentStepResult;
    private FlowExecutionController parent;
    @FXML private VBox root;
    // header elements
    private ElementController currentFlowUUID;
    private ElementController currentFlowName;
    private ElementController currentFlowStatus;
    private ElementController currentFlowDuration;
    private ElementController spacer;
    private List<ElementController> stepElementControllers;
    private List<Parent> stepElements;
    private boolean runEnded;

    public FlowExecutionElementsController() {
    }

    @FXML public void initialize() {
        // initialize step element list
        stepElementControllers = new ArrayList<>();
        stepElements = new ArrayList<>();

        runEnded = false;

        // setup bindings and listeners
        currentFlowResult = new SimpleObjectProperty<>();
        currentStepResult = new SimpleObjectProperty<>();

        currentFlowResult.bind(FlowExecutor.lastFlowResult);
        currentStepResult.bind(FlowExecutor.lastStepResult);

        currentFlowResult.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                synchronized (newValue) {
                    if (!newValue.equals(oldValue)) {
                        reset();
                        updateHeader();
                    } else {
                        updateHeader();
                        updateElements();
                    }
                }
            }
        });
        currentStepResult.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                synchronized (newValue) {
                    updateHeader();
                    updateElements();
                }
            }
        });
    }

    public void setParent(FlowExecutionController parent) {
        this.parent = parent;
    }

    public void reset() {
        runEnded = false;
        root.getChildren().clear();
        stepElements.clear();
        stepElementControllers.clear();
    }

    private void updateHeader() {
        root.getChildren().clear();
        try {
            // UUID
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            Parent loaded = loader.load();
            currentFlowUUID = loader.getController();
            currentFlowUUID.setPropertyName("Run UUID:");
            Platform.runLater(() -> currentFlowUUID.setPropertyValue(currentFlowResult.getValue().runId()));
            currentFlowUUID.removeButton();
            root.getChildren().add(loaded);
            // Name
            loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            loaded = loader.load();
            currentFlowName = loader.getController();
            currentFlowName.setPropertyName("Flow Name:");
            Platform.runLater(() -> currentFlowName.setPropertyValue(currentFlowResult.getValue().name()));
            currentFlowName.removeButton();
            root.getChildren().add(loaded);
            // Status
            loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            loaded = loader.load();
            currentFlowStatus = loader.getController();
            currentFlowStatus.setPropertyName("Status\\Result:");
            Platform.runLater(() -> currentFlowStatus.setPropertyValue(currentFlowResult.getValue().result().toString()));
            currentFlowStatus.removeButton();
            root.getChildren().add(loaded);
            // Duration
            loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            loaded = loader.load();
            currentFlowDuration = loader.getController();
            currentFlowDuration.setPropertyName("Duration:");
            Platform.runLater(() -> currentFlowDuration.setPropertyValue(currentFlowResult.getValue().duration().toMillis() + " ms"));
            currentFlowDuration.removeButton();
            root.getChildren().add(loaded);
            // Spacing from step elements
            loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            loaded = loader.load();
            spacer = loader.getController();
            spacer.setPropertyName("");
            spacer.setPropertyValue("");
            spacer.removeButton();
            root.getChildren().add(loaded);

            if(!currentFlowResult.getValue().result().equals(FlowResult.RUNNING))
                onRunEnded();
        } catch (IOException ignored) {
        }
    }

    private void updateElements() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ElementController.class.getResource("Element.fxml"));
        try {
            Parent loaded = loader.load();
            ElementController currentStep = loader.getController();
            final IStepRunResult currentStepResultValue = currentStepResult.getValue();
            currentStep.setPropertyName("Step:");
            currentStep.setPropertyValue(currentStepResultValue.name());
            currentStep.setContext(currentStepResultValue);
            currentStep.setOnAction(this::selectStep);
            stepElementControllers.add(currentStep);
            stepElements.add(loaded);
            root.getChildren().addAll(stepElements);
        } catch (IOException ignored) {
        }
    }

    public void onRunEnded() {
        if (!runEnded) {
            parent.updateContinuations();
            runEnded = true;
        }
    }

    public void selectStep(IStepRunResult step) {
        parent.selectStepForDetails(step);
    }
}
