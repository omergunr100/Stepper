package com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements;

import com.main.stepper.application.resources.dataview.list.ListViewController;
import com.main.stepper.application.resources.dataview.relation.RelationViewController;
import com.main.stepper.application.resources.fxml.tabs.executionshistory.ExecutionHistoryScreenController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.executionelements.element.ElementController;
import com.main.stepper.application.resources.fxml.tabs.flowsexecution.tab.FlowExecutionController;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.data.implementation.file.FileData;
import com.main.stepper.data.implementation.list.datatype.GenericList;
import com.main.stepper.data.implementation.mapping.api.PairData;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import javafx.application.Platform;
import javafx.beans.property.Property;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowExecutionElementsController {
    private Property<IFlowRunResult> currentFlowResult;
    private Property<IStepRunResult> currentStepResult;
    private FlowExecutionController flowExecutionController;
    private ExecutionHistoryScreenController executionHistoryScreenController;
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
    }

    public void autoUpdate() {
        currentFlowResult.bind(FlowExecutor.lastFlowResult);
        currentStepResult.bind(FlowExecutor.lastStepResult);

        currentFlowResult.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                synchronized (newValue) {
                    if (!newValue.equals(oldValue)) {
                        reset();
                        updateHeader();
                        loadFlowInputsFormalOutputs();
                    } else {
                        updateHeader();
                        updateElements();
                        loadFlowInputsFormalOutputs();
                    }
                }
            }
        });
        currentStepResult.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                synchronized (newValue) {
                    updateHeader();
                    updateElements();
                    loadFlowInputsFormalOutputs();
                }
            }
        });
    }

    public void setFlowExecutionController(FlowExecutionController flowExecutionController) {
        this.flowExecutionController = flowExecutionController;
        this.executionHistoryScreenController = null;
    }

    public void setExecutionHistoryScreenController(ExecutionHistoryScreenController executionHistoryScreenController) {
        this.executionHistoryScreenController = executionHistoryScreenController;
        this.flowExecutionController = null;
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
        IFlowRunResult flowResult = currentFlowResult.getValue();
        // get map of formal outputs and values
        Map<IDataIO, Object> flowFormalOutputs = flowResult.flowOutputs();
        // get map of user inputs and values
        Map<IDataIO, Object> flowUserInputs = flowResult.userInputs();
        // get map of all internal outputs and values
        Map<IDataIO, Object> flowInternalOutputs = flowResult.internalOutputs();

        // create list of elements for user inputs and add to components
        Label userInputsLabel = new Label("User inputs:");
        root.getChildren().add(userInputsLabel);
        for (Map.Entry<IDataIO, Object> entry : flowUserInputs.entrySet()) {
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
        for (Map.Entry<IDataIO, Object> entry : flowFormalOutputs.entrySet()) {
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
        for (Map.Entry<IDataIO, Object> entry : flowInternalOutputs.entrySet()) {
            root.getChildren().add(makeDataView(entry.getKey(), entry.getValue()));
            Label spacer = new Label();
            spacer.setMinHeight(10);
            root.getChildren().add(spacer);
        }
    }

    public void forceLoadAllElementsFrom(IFlowRunResult flowRunResult) {
        reset();
        currentFlowResult.setValue(flowRunResult);
        for (int i = 0; i < flowRunResult.stepRunResults().size(); i++) {
            updateHeader();
            currentStepResult.setValue(flowRunResult.stepRunResults().get(i));
            updateElements();
        }
        loadFlowInputsFormalOutputs();
    }

    public void onRunEnded() {
        if (flowExecutionController != null && !runEnded) {
            flowExecutionController.updateContinuations();
            runEnded = true;
        }
    }

    public void selectStep(IStepRunResult step) {
        if (flowExecutionController != null)
            flowExecutionController.selectStepForDetails(step);
        else if (executionHistoryScreenController != null)
            executionHistoryScreenController.selectStepRunDetails(step);
    }

    private Parent makeDataView(IDataIO data, Object blob) {
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
        dataNameValue.setText(data.getName());
        dataNameBox.getChildren().addAll(dataName, dataNameValue);
        dataView.getChildren().add(dataNameBox);

        // dataType
        HBox dataTypeBox = new HBox();
        dataTypeBox.setSpacing(10);
        Label dataType = new Label("Data type: ");
        TextField dataTypeValue = new TextField();
        dataTypeValue.setEditable(false);
        dataTypeValue.setText(data.getDataDefinition().getName());
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

        // optional created by step
        if (data.getNecessity().equals(DataNecessity.NA)) {
            HBox dataCreatedByStepBox = new HBox();
            dataCreatedByStepBox.setSpacing(10);
            Label dataCreatedByStep = new Label("Created by step: ");
            TextField dataCreatedByStepValue = new TextField();
            dataCreatedByStepValue.setEditable(false);
            dataCreatedByStepValue.setText(currentFlowResult.getValue().flowDefinition().reverseMapDataIO(data).name());
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
                if (currentFlowResult.getValue().result().equals(FlowResult.RUNNING))
                    blob = "Data not available";
                else
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
                if (currentFlowResult.getValue().result().equals(FlowResult.RUNNING))
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
                if (currentFlowResult.getValue().result().equals(FlowResult.RUNNING))
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
