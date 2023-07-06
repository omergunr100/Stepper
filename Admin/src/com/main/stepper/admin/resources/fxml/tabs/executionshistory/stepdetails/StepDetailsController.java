package com.main.stepper.admin.resources.fxml.tabs.executionshistory.stepdetails;

import com.main.stepper.admin.resources.dataview.list.ListViewController;
import com.main.stepper.admin.resources.dataview.relation.RelationViewController;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.data.implementation.file.FileData;
import com.main.stepper.data.implementation.list.datatype.GenericList;
import com.main.stepper.data.implementation.mapping.api.PairData;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.step.definition.api.IStepDefinition;
import com.main.stepper.step.execution.api.IStepExecutionContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML private Label summaryLabel;
    @FXML private VBox logBox;
    @FXML private VBox dataBox;

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
        // clear previous data
        dataBox.getChildren().clear();
        logBox.getChildren().clear();
        // update header
        nameLabel.setText(currentStepResult.name());
        resultLabel.setText(currentStepResult.result().toString());
        startTimeLabel.setText(dateTimeFormatter.format(currentStepResult.startTime()));
        endTimeLabel.setText(dateTimeFormatter.format(currentStepResult.startTime().plusMillis(currentStepResult.duration().toMillis())));
        durationLabel.setText(String.valueOf(currentStepResult.duration().toMillis()));
        summaryLabel.setText(currentStepResult.summary());
        // update inputs and outputs
        IStepDefinition step = currentStepResult.stepDefinition();
        IStepExecutionContext context = currentStepResult.context();
        List<IDataIO> inputs = step.getInputs();
        List<IDataIO> outputs = step.getOutputs();

        for (int i = 0; i < inputs.size(); i++) {
            dataBox.getChildren().add(makeDataView(inputs.get(i), currentStepResult.context()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            dataBox.getChildren().add(spacer);
            dataBox.getChildren().add(new Separator());
        }
        for (int i = 0; i < outputs.size(); i++) {
            dataBox.getChildren().add(makeDataView(outputs.get(i), currentStepResult.context()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            dataBox.getChildren().add(spacer);
            dataBox.getChildren().add(new Separator());
        }

        // update logs
        List<Log> logs = context.getLogs();
        for (int i = 0; i < logs.size(); i++) {
            HBox logView = new HBox();
            Label log = new Label(logs.get(i).toString());
            logView.getChildren().add(log);
            logBox.getChildren().add(logView);
        }

        root.setVisible(true);
    }

    public void reset() {
        currentStepResult = null;
        root.setVisible(false);
    }

    private static Parent makeDataView(IDataIO data, IStepExecutionContext context) {
        if (data == null)
            return null;
        IDataIO aliasedDataIO = context.getAliasedDataIO(data);
        VBox dataView = new VBox();
        dataView.setSpacing(10);
        // dataName
        HBox dataNameBox = new HBox();
        dataNameBox.setSpacing(10);
        Label dataName = new Label("Data name: ");
        TextField dataNameValue = new TextField();
        dataNameValue.setEditable(false);
        dataNameValue.setText(aliasedDataIO.getName());
        dataNameBox.getChildren().addAll(dataName, dataNameValue);
        dataView.getChildren().add(dataNameBox);

        // dataType
        HBox dataTypeBox = new HBox();
        dataTypeBox.setSpacing(10);
        Label dataType = new Label("Data type: ");
        TextField dataTypeValue = new TextField();
        dataTypeValue.setEditable(false);
        dataTypeValue.setText(aliasedDataIO.getDataDefinition().getName());
        dataTypeBox.getChildren().addAll(dataType, dataTypeValue);
        dataView.getChildren().add(dataTypeBox);

        // dataRequirement
        HBox dataRequirementBox = new HBox();
        dataRequirementBox.setSpacing(10);
        Label dataRequirement = new Label("Data requirement: ");
        TextField dataRequirementValue = new TextField();
        dataRequirementValue.setEditable(false);
        dataRequirementValue.setText(data.getNecessity().equals(DataNecessity.NA) ? "Output" : data.getNecessity().toString());
        dataRequirementBox.getChildren().addAll(dataRequirement, dataRequirementValue);
        dataView.getChildren().add(dataRequirementBox);

        // dataValue
        HBox dataValueBox = new HBox();
        dataValueBox.setSpacing(10);
        Label dataValue = new Label("Data value: ");
        dataView.getChildren().add(dataValue);
        Parent dataValueView = null;
        Object blob = context.getInput(data, data.getDataDefinition().getType());
        if (
                String.class.isAssignableFrom(data.getDataDefinition().getType())
                || Double.class.isAssignableFrom(data.getDataDefinition().getType())
                || Integer.class.isAssignableFrom(data.getDataDefinition().getType())
                || PairData.class.isAssignableFrom(data.getDataDefinition().getType())
                || FileData.class.isAssignableFrom(data.getDataDefinition().getType())
                || ZipperEnumData.class.isAssignableFrom(data.getDataDefinition().getType())
        ) {
            HBox valueBox = new HBox();
            valueBox.setSpacing(10);
            TextField dataValueField = new TextField();
            dataValueField.setEditable(false);
            if (blob == null){
                blob = "Not created due to failure in flow";
            }
            dataValueField.setText(blob.toString());
            dataValueView = dataValueField;
        }
        else if (
                GenericList.class.isAssignableFrom(data.getDataDefinition().getType())
        ) {
            if (blob == null) {
                TextField dataValueField = new TextField();
                dataValueField.setEditable(false);
                dataValueField.setText("Not created due to failure in flow");
                dataValueView = dataValueField;
            }
            else {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(ListViewController.class.getResource("ListView.fxml"));
                Parent listView = null;
                try {
                    listView = loader.load();
                    ListViewController controller = loader.getController();
                    controller.loadList((GenericList) blob);
                } catch (IOException ignored) {
                }
                dataValueView = listView;
            }
        }
        else if (
                Relation.class.isAssignableFrom(data.getDataDefinition().getType())
        ) {
            if (blob == null) {
                TextField dataValueField = new TextField();
                dataValueField.setEditable(false);
                dataValueField.setText("Not created due to failure in flow");
                dataValueView = dataValueField;
            }
            else {
                Button openButton = new Button("Show data");
                final Object finalBlob = blob;
                openButton.setOnAction(event -> {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(RelationViewController.class.getResource("RelationView.fxml"));
                    try {
                        Parent relationView = loader.load();
                        RelationViewController controller = loader.getController();
                        controller.updateTable((Relation) finalBlob);
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setTitle("Relation view");
                        stage.setScene(new Scene(relationView));
                        stage.show();
                    } catch (IOException ignored) {
                    }
                });
                dataValueView = openButton;
            }
        }
        dataValueBox.getChildren().addAll(dataValue, dataValueView);
        dataView.getChildren().add(dataValueBox);
        // return generated component
        return dataView;
    }
}
