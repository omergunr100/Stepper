package com.main.stepper.client.resources.fxml.reusable.executionstepsbox;

import com.main.stepper.client.resources.fxml.reusable.executionstepsbox.option.OptionController;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import com.main.stepper.shared.structures.step.StepUsageDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionStepsBoxController {
    @FXML private VBox root;

    private SimpleObjectProperty<OptionController> currentlySelectedOption = new SimpleObjectProperty<>(null);
    private OptionController flowOptionController = null;
    private Map<String, OptionController> stepOptionControllers = new HashMap<>();

    private SimpleObjectProperty<FlowRunResultDTO> runningFlowResult = null;
    private SimpleBooleanProperty isFlowSelected = null;
    private SimpleObjectProperty<StepRunResultDTO> selectedStepResult = null;

    public ExecutionStepsBoxController() {
    }

    @FXML public void initialize() {
        currentlySelectedOption.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null)
                oldValue.deselect();
        });
    }

    public void setBindings(SimpleObjectProperty<FlowRunResultDTO> runningFlowResult, SimpleObjectProperty<StepRunResultDTO> selectedStepResult, SimpleBooleanProperty isFlowSelected) {
        this.runningFlowResult = runningFlowResult;
        this.selectedStepResult = selectedStepResult;
        this.isFlowSelected = isFlowSelected;

        this.runningFlowResult.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                reset();
            }
            else if (oldValue == null) {
                load();
            }
            else if (oldValue.runId().equals(newValue.runId())) {
                update();
            }
            else {
                this.isFlowSelected.set(false);
                this.selectedStepResult.set(null);
                load();
            }
        });
    }

    public void reset() {
        root.getChildren().clear();
        flowOptionController = null;
        stepOptionControllers.clear();
    }

    private void load() {
        // clear previous information
        reset();
        loadFlowOption();
        loadStepOptions();
    }

    private void loadFlowOption() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(OptionController.class.getResource("Option.fxml"));
        try {
            Parent node = loader.load();
            flowOptionController = loader.getController();

            flowOptionController.setName("Flow: " + runningFlowResult.get().name());
            flowOptionController.setStatus(runningFlowResult.get().result());
            flowOptionController.setOnAction(() -> {
                selectedStepResult.set(null);
                isFlowSelected.set(true);
                currentlySelectedOption.set(flowOptionController);
            });

            root.getChildren().add(node);
        } catch (IOException ignored) {
        }
    }

    private void loadStepOptions() {
        ArrayList<StepUsageDTO> steps = runningFlowResult.get().flowInfo().steps();
        for (int i = 0; i < steps.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(OptionController.class.getResource("Option.fxml"));
            try {
                Parent node = loader.load();
                OptionController stepOptionController = loader.getController();

                stepOptionController.setName("Step " + (i + 1) + ": " + steps.get(i).name());
                stepOptionController.setStatus(FlowResult.RUNNING);
                stepOptionController.setOnAction(stepOptionController::deselect);

                stepOptionControllers.put(steps.get(i).name(), stepOptionController);
                root.getChildren().add(node);
            } catch (IOException ignored) {
            }
        }
        updateStepOptions();
    }

    private void update() {
        // update information
        updateFlowOption();
        updateStepOptions();
    }

    private void updateFlowOption() {
        try {
            flowOptionController.setStatus(runningFlowResult.get().result());
        } catch (IOException e) {
        }
    }

    private void updateStepOptions() {
        FlowRunResultDTO result = runningFlowResult.get();
        for (StepRunResultDTO step : result.stepRunResults()) {
            OptionController stepOptionController = stepOptionControllers.get(step.name());
            try {
                stepOptionController.setStatus(step.result());
            } catch (IOException e) {
            }
            if (selectedStepResult.isNotNull().get() && selectedStepResult.get().name().equals(step.name())) {
                Platform.runLater(() -> {
                    selectedStepResult.set(null);
                    selectedStepResult.set(step);
                    currentlySelectedOption.set(stepOptionController);
                });
            }
            stepOptionController.setOnAction(() -> {
                Platform.runLater(() -> {
                    isFlowSelected.set(false);
                    selectedStepResult.set(step);
                });
            });
        }
    }
}
