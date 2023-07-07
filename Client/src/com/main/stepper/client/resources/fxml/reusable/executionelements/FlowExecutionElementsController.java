package com.main.stepper.client.resources.fxml.reusable.executionelements;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.dataview.list.ListViewController;
import com.main.stepper.client.resources.dataview.relation.RelationViewController;
import com.main.stepper.client.resources.fxml.reusable.executionelements.element.ElementController;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.data.implementation.file.FileData;
import com.main.stepper.data.implementation.list.datatype.GenericList;
import com.main.stepper.data.implementation.mapping.api.PairData;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowExecutionElementsController {
    private SimpleObjectProperty<FlowRunResultDTO> selectedFlowResult;
    private StepRunResultDTO selectedStepResult;
    private SimpleObjectProperty<StepRunResultDTO> currentStepResult;
    @FXML private VBox root;
    // header elements
    private ElementController currentFlowUUID;
    private ElementController currentFlowName;
    private ElementController currentFlowStatus;
    private ElementController currentFlowDuration;
    private ElementController spacer;
    private List<ElementController> stepElementControllers;
    private List<Parent> stepElements;

    public FlowExecutionElementsController() {
    }

    public void setBindings(SimpleObjectProperty<FlowRunResultDTO> flowRunResultProperty, SimpleObjectProperty<StepRunResultDTO> stepRunResultProperty) {
        selectedFlowResult = flowRunResultProperty;
        currentStepResult = stepRunResultProperty;
        selectedFlowResult.addListener((observable, oldValue, newValue) -> onSelectedFlowChange());
    }

    @FXML public void initialize() {
        // initialize step element list
        stepElementControllers = new ArrayList<>();
        stepElements = new ArrayList<>();

        // setup bindings and listeners
        selectedFlowResult = new SimpleObjectProperty<>();
        currentStepResult = new SimpleObjectProperty<>();
        selectedStepResult = null;
    }

    public void reset() {
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
            Platform.runLater(() -> currentFlowUUID.setPropertyValue(selectedFlowResult.getValue().runId().toString()));
            currentFlowUUID.removeButton();
            root.getChildren().add(loaded);
            // Name
            loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            loaded = loader.load();
            currentFlowName = loader.getController();
            currentFlowName.setPropertyName("Flow Name:");
            Platform.runLater(() -> currentFlowName.setPropertyValue(selectedFlowResult.getValue().name()));
            currentFlowName.removeButton();
            root.getChildren().add(loaded);
            // Status
            loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            loaded = loader.load();
            currentFlowStatus = loader.getController();
            currentFlowStatus.setPropertyName("Status\\Result:");
            Platform.runLater(() -> currentFlowStatus.setPropertyValue(selectedFlowResult.getValue().result().toString()));
            currentFlowStatus.removeButton();
            root.getChildren().add(loaded);
            // Duration
            loader = new FXMLLoader();
            loader.setLocation(ElementController.class.getResource("Element.fxml"));
            loaded = loader.load();
            currentFlowDuration = loader.getController();
            currentFlowDuration.setPropertyName("Duration:");
            Platform.runLater(() -> {
                if (selectedFlowResult.getValue().duration() == null)
                    currentFlowDuration.setPropertyValue(Duration.between(selectedFlowResult.getValue().startTime(), Instant.now()).toMillis() + " ms");
                else
                    currentFlowDuration.setPropertyValue(selectedFlowResult.getValue().duration().toMillis() + " ms");
            });
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
        } catch (IOException ignored) {
        }
    }

    private void updateElements() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ElementController.class.getResource("Element.fxml"));
        try {
            Parent loaded = loader.load();
            ElementController currentStep = loader.getController();
            final StepRunResultDTO currentStepResultValue = selectedStepResult;
            currentStep.setPropertyName("Step:");
            currentStep.setPropertyValue(currentStepResultValue.alias() == null ? currentStepResultValue.name() : currentStepResultValue.alias());
            currentStep.setContext(currentStepResultValue);
            currentStep.setOnAction(this::selectStep);
            stepElementControllers.add(currentStep);
            stepElements.add(loaded);
            root.getChildren().addAll(stepElements);
        } catch (IOException ignored) {
        }
    }

    private void loadFlowInputsFormalOutputs() {
        FlowRunResultDTO flowResult = selectedFlowResult.getValue();
        // get map of formal outputs and values
        Map<DataIODTO, Object> flowFormalOutputs = flowResult.flowOutputs();
        // get map of user inputs and values
        Map<DataIODTO, Object> flowUserInputs = flowResult.userInputs();
        // get map of all internal outputs and values
        Map<DataIODTO, Object> flowInternalOutputs = flowResult.internalOutputs();

        // create list of elements for user inputs and add to components
        Label userInputsLabel = new Label("User inputs:");
        root.getChildren().add(userInputsLabel);
        for (Map.Entry<DataIODTO, Object> entry : flowUserInputs.entrySet()) {
            root.getChildren().add(makeDataView(entry.getKey(), entry.getValue()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            root.getChildren().add(spacer);
        }
        Separator separator = new Separator();
        root.getChildren().add(separator);

        // create list of elements for formal outputs and add to components
        Label formalOutputsLabel = new Label("Formal outputs:");
        root.getChildren().add(formalOutputsLabel);
        for (Map.Entry<DataIODTO, Object> entry : flowFormalOutputs.entrySet()) {
            root.getChildren().add(makeDataView(entry.getKey(), entry.getValue()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            root.getChildren().add(spacer);
        }
        Separator separator2 = new Separator();
        root.getChildren().add(separator2);

        // create list of elements for all outputs
        Label internalOutputsLabel = new Label("Internal outputs:");
        root.getChildren().add(internalOutputsLabel);
        for (Map.Entry<DataIODTO, Object> entry : flowInternalOutputs.entrySet()) {
            root.getChildren().add(makeDataView(entry.getKey(), entry.getValue()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            root.getChildren().add(spacer);
        }
    }

    public void onSelectedFlowChange() {
        reset();
        if (selectedFlowResult.isNull().get()) {
            currentStepResult.set(null);
            return;
        }
        for (int i = 0; i < selectedFlowResult.get().stepRunResults().size(); i++) {
            updateHeader();
            selectedStepResult = selectedFlowResult.get().stepRunResults().get(i);
            updateElements();
        }
        loadFlowInputsFormalOutputs();
    }

    public void selectStep(StepRunResultDTO step) {
        currentStepResult.set(step);
    }

    private Parent makeDataView(DataIODTO data, Object blob) {
        if (data == null)
            return null;
        VBox dataView = new VBox();
        dataView.setSpacing(10);
        // dataName
        HBox dataNameBox = new HBox();
        dataNameBox.setSpacing(10);
        Label dataName = new Label("Data name: ");
        TextField dataNameValue = new TextField();
        dataNameValue.setEditable(false);
        dataNameValue.setText(data.name());
        dataNameBox.getChildren().addAll(dataName, dataNameValue);
        dataView.getChildren().add(dataNameBox);

        // dataType
        HBox dataTypeBox = new HBox();
        dataTypeBox.setSpacing(10);
        Label dataType = new Label("Data type: ");
        TextField dataTypeValue = new TextField();
        dataTypeValue.setEditable(false);
        dataTypeValue.setText(data.type().getName());
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

        // optional created by step
        if (data.necessity().equals(DataNecessity.NA)) {
            HBox dataCreatedByStepBox = new HBox();
            dataCreatedByStepBox.setSpacing(10);
            Label dataCreatedByStep = new Label("Created by step: ");
            TextField dataCreatedByStepValue = new TextField();
            dataCreatedByStepValue.setEditable(false);
            // todo: ensure it works correctly
            dataCreatedByStepValue.setText(selectedFlowResult.getValue().flowInfo().producerStep(data).name());
            dataCreatedByStepBox.getChildren().addAll(dataCreatedByStep, dataCreatedByStepValue);
            dataView.getChildren().add(dataCreatedByStepBox);
        }

        // dataValue
        HBox dataValueBox = new HBox();
        dataValueBox.setSpacing(10);
        Label dataValue = new Label("Data value: ");
        dataView.getChildren().add(dataValue);
        Parent dataValueView = null;
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
                if (selectedFlowResult.getValue().result().equals(FlowResult.RUNNING))
                    blob = "Data not available";
                else
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
                if (selectedFlowResult.getValue().result().equals(FlowResult.RUNNING))
                    dataValueField.setText("Data not available");
                else
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
                    controller.loadList((List) blob);
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
                if (selectedFlowResult.getValue().result().equals(FlowResult.RUNNING))
                    dataValueField.setText("Data not available");
                else
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
