package com.main.stepper.client.resources.fxml.reusable.stepdetails;

import com.main.stepper.client.resources.dataview.list.ListViewController;
import com.main.stepper.client.resources.dataview.relation.RelationViewController;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.data.implementation.file.FileData;
import com.main.stepper.data.implementation.list.datatype.GenericList;
import com.main.stepper.data.implementation.mapping.api.PairData;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import com.main.stepper.shared.structures.step.StepExecutionContextDTO;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import com.main.stepper.step.definition.api.IStepDefinition;
import com.main.stepper.step.execution.api.IStepExecutionContext;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.SimpleObjectProperty;
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

    private SimpleObjectProperty<StepRunResultDTO> currentStepResult = new SimpleObjectProperty<>();
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

    public void setBinding(SimpleObjectProperty<StepRunResultDTO> property) {
        currentStepResult.bind(property);
        currentStepResult.addListener((observable, oldValue, newValue) -> onStepChange());
    }

    public void onStepChange() {
        if (currentStepResult.isNull().get()) {
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
        nameLabel.setText(currentStepResult.get().name());
        resultLabel.setText(currentStepResult.get().result().toString());
        startTimeLabel.setText(dateTimeFormatter.format(currentStepResult.get().startTime()));
        endTimeLabel.setText(dateTimeFormatter.format(currentStepResult.get().startTime().plusMillis(currentStepResult.get().duration().toMillis())));
        durationLabel.setText(String.valueOf(currentStepResult.get().duration().toMillis()));
        summaryLabel.setText(currentStepResult.get().summary());
        // update inputs and outputs
        StepDefinitionDTO step = currentStepResult.get().step();
        StepExecutionContextDTO context = currentStepResult.get().context();
        List<DataIODTO> inputs = step.inputs();
        List<DataIODTO> outputs = step.outputs();

        for (int i = 0; i < inputs.size(); i++) {
            dataBox.getChildren().add(makeDataView(inputs.get(i), currentStepResult.get().context()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            dataBox.getChildren().add(spacer);
            dataBox.getChildren().add(new Separator());
        }
        for (int i = 0; i < outputs.size(); i++) {
            dataBox.getChildren().add(makeDataView(outputs.get(i), currentStepResult.get().context()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            dataBox.getChildren().add(spacer);
            dataBox.getChildren().add(new Separator());
        }

        // update logs
        List<Log> logs = context.getLogs();
        for (int i = 0; i < logs.size(); i++) {
            HBox logView = new HBox();
            Label log = new Label();
            logView.getChildren().add(log);
            logBox.getChildren().add(logView);
            log.setText(logs.get(i).toString());
            // todo: figure out why label is cutting out instead of scrolling
        }

        root.setVisible(true);
    }

    public void reset() {
        root.setVisible(false);
    }

    private static Parent makeDataView(DataIODTO data, StepExecutionContextDTO context) {
        if (data == null)
            return null;
        DataIODTO aliasedDataIO = context.getAliasedDataIO(data);
        VBox dataView = new VBox();
        dataView.setSpacing(10);
        // dataName
        HBox dataNameBox = new HBox();
        dataNameBox.setSpacing(10);
        Label dataName = new Label("Data name: ");
        TextField dataNameValue = new TextField();
        dataNameValue.setEditable(false);
        dataNameValue.setText(aliasedDataIO.name());
        dataNameBox.getChildren().addAll(dataName, dataNameValue);
        dataView.getChildren().add(dataNameBox);

        // dataType
        HBox dataTypeBox = new HBox();
        dataTypeBox.setSpacing(10);
        Label dataType = new Label("Data type: ");
        TextField dataTypeValue = new TextField();
        dataTypeValue.setEditable(false);
        dataTypeValue.setText(aliasedDataIO.type().getName());
        dataTypeBox.getChildren().addAll(dataType, dataTypeValue);
        dataView.getChildren().add(dataTypeBox);

        // dataRequirement
        HBox dataRequirementBox = new HBox();
        dataRequirementBox.setSpacing(10);
        Label dataRequirement = new Label("Data requirement: ");
        TextField dataRequirementValue = new TextField();
        dataRequirementValue.setEditable(false);
        dataRequirementValue.setText(data.necessity().equals(DataNecessity.NA) ? "Output" : data.necessity().toString());
        dataRequirementBox.getChildren().addAll(dataRequirement, dataRequirementValue);
        dataView.getChildren().add(dataRequirementBox);

        // dataValue
        HBox dataValueBox = new HBox();
        dataValueBox.setSpacing(10);
        Label dataValue = new Label("Data value: ");
        dataView.getChildren().add(dataValue);
        Parent dataValueView = null;
        Object blob = context.getInput(data, data.type().getType());
        if (
                String.class.isAssignableFrom(data.type().getType())
                || Double.class.isAssignableFrom(data.type().getType())
                || Integer.class.isAssignableFrom(data.type().getType())
                || PairData.class.isAssignableFrom(data.type().getType())
                || FileData.class.isAssignableFrom(data.type().getType())
                || ZipperEnumData.class.isAssignableFrom(data.type().getType())
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
                GenericList.class.isAssignableFrom(data.type().getType())
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
                Relation.class.isAssignableFrom(data.type().getType())
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
