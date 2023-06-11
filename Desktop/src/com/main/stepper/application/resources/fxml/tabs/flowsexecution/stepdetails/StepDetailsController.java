package com.main.stepper.application.resources.fxml.tabs.flowsexecution.stepdetails;

import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.step.definition.api.IStepDefinition;
import com.main.stepper.step.execution.api.IStepExecutionContext;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class StepDetailsController {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    private IStepRunResult currentStepResult = null;
    @FXML private GridPane root;
    @FXML private Label nameLabel;
    @FXML private Label resultLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label durationLabel;

    public StepDetailsController() {
    }

    @FXML public void initialize() {
        root.setVisible(false);
    }

    public void setStep(IStepRunResult step) {
        currentStepResult = step;
        if (step == null) {
            reset();
        }
        else {
            update();
        }
    }

    private void update() {
        // update header
        nameLabel.setText(currentStepResult.name());
        resultLabel.setText(currentStepResult.result().toString());
        startTimeLabel.setText(dateTimeFormatter.format(currentStepResult.startTime()));
        endTimeLabel.setText(dateTimeFormatter.format(currentStepResult.startTime().plusMillis(currentStepResult.duration().toMillis())));
        durationLabel.setText(String.valueOf(currentStepResult.duration().toMillis()));
        // update inputs and outputs
        IStepDefinition step = currentStepResult.stepDefinition();
        IStepExecutionContext context = currentStepResult.context();
        List<IDataIO> inputs = step.getInputs();
        List<IDataIO> outputs = step.getOutputs();

        for (int i = 0; i < inputs.size(); i++) {
            // todo: make input presentation according to type
        }
        for (int i = 0; i < outputs.size(); i++) {
            // todo: make output presentation according to type
        }

        // update logs
        List<Log> logs = context.getLogs();
        for (int i = 0; i < logs.size(); i++) {
            // todo: present logs
        }

        root.setVisible(true);
    }

    public void reset() {
        currentStepResult = null;
        root.setVisible(false);
    }
}
