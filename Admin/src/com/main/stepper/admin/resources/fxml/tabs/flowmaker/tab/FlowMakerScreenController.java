package com.main.stepper.admin.resources.fxml.tabs.flowmaker.tab;

import com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview.ComponentDragController;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.flowbuilder.FlowBuilderController;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.stepproperties.StepPropertiesController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class FlowMakerScreenController {
    @FXML private ComponentDragController componentsViewController;
    @FXML private FlowBuilderController flowBuilderController;
    @FXML private StepPropertiesController stepPropertiesController;

    @FXML private ColumnConstraints propertiesCol;

    @FXML private Text selectedStepText;
    @FXML private TextField widthTextField;
    @FXML private TextField heightTextField;

    private SimpleIntegerProperty widthProperty = new SimpleIntegerProperty();
    private SimpleIntegerProperty heightProperty = new SimpleIntegerProperty();

    public FlowMakerScreenController() {
    }

    @FXML public void initialize() {
        selectedStepText.textProperty().bind(Bindings.createStringBinding(() -> selectedStep.isNull().get() ? (selectedDraggableStepForMoving.isNull().get() ? "" : ("Moving: " + selectedDraggableStepForMoving.get().getAlias())) : selectedStep.get().name(), selectedStep, selectedDraggableStepForMoving));
        widthTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                widthTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        heightTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                heightTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        widthProperty.bind(Bindings.createIntegerBinding(() -> widthTextField.getText().isEmpty() ? 0 : Integer.parseInt(widthTextField.getText()), widthTextField.textProperty()));
        heightProperty.bind(Bindings.createIntegerBinding(() -> heightTextField.getText().isEmpty() ? 0 : Integer.parseInt(heightTextField.getText()), heightTextField.textProperty()));

        flowBuilderController.bindSize(widthProperty, heightProperty);

        // set bindings for shared properties
        selectedDraggableStepForMoving.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedStep.set(null);
            }
        });
        selectedStep.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedDraggableStepForMoving.set(null);
            }
        });
        selectedDraggableStepForProperties.addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                propertiesCol.setMaxWidth(-1);
            else
                propertiesCol.setMaxWidth(0);
        });
    }
}
