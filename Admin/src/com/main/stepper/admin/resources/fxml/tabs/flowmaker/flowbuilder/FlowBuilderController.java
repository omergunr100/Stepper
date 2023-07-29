package com.main.stepper.admin.resources.fxml.tabs.flowmaker.flowbuilder;

import com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview.datalink.DataLink;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.draggablestep.DraggableStep;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.flowbuilder.BuiltFlowDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class FlowBuilderController {
    @FXML private AnchorPane root;

    private SimpleDoubleProperty maxWidthOfElements = new SimpleDoubleProperty(0);
    private SimpleDoubleProperty maxHeightOfElements = new SimpleDoubleProperty(0);

    public FlowBuilderController() {
    }

    @FXML public void initialize() {
        root.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) onMousePress(event.getX(), event.getY());
        });
    }

    public BuiltFlowDTO build() {
        // todo: implement logic to build the flow from scene graph
        return null;
    }

    public void onMousePress(double x, double y) {
        // check if there is a step loaded
        DraggableStep step = null;
        if (selectedStep.isNotNull().get()) {
            // if step is loaded add it to the scene
            step = new DraggableStep(selectedStep.get());
            step.setIsInFlow(true);
            root.getChildren().add(step);
            // remove step selection
            selectedStep.set(null);
            // add listener for step removal
            final DraggableStep finalStep = step;
            step.deleteProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    root.getChildren().remove(finalStep);
                    if (selectedDraggableStepForMoving.get() != null) {
                        if (selectedDraggableStepForMoving.get().equals(finalStep)) {
                            selectedDraggableStepForMoving.set(null);
                        }
                    }
                    if (selectedDraggableStepForProperties.get() != null) {
                        if (selectedDraggableStepForProperties.get().equals(finalStep)) {
                            selectedDraggableStepForProperties.set(null);
                        }
                    }
                }
            });

            ObservableMap<DataIODTO, DataLink> dataLinkMap = FXCollections.observableHashMap();
            for (DataIODTO dto : step.step().inputs()) {
                dataLinkMap.put(dto, new DataLink(dto));
            }
            for (DataIODTO dto : step.step().outputs()) {
                dataLinkMap.put(dto, new DataLink(dto));
            }
            stepToDataLinkMap.put(step, dataLinkMap);
        }
        else if (selectedDraggableStepForMoving.isNotNull().get()) {
            // set step to the selected draggable step
            step = selectedDraggableStepForMoving.get();
            selectedDraggableStepForMoving.set(null);
        }

        if (step != null) {
            step.setLayoutX(x);
            step.setLayoutY(y);
            recalculateMax();
        }
    }

    public void recalculateMax() {
        maxWidthOfElements.set(root.getChildren().stream().mapToDouble(Node::getLayoutX).max().orElse(0));
        maxHeightOfElements.set(root.getChildren().stream().mapToDouble(Node::getLayoutY).max().orElse(0));
    }

    public void bindSize(SimpleIntegerProperty width, SimpleIntegerProperty height) {
        root.minWidthProperty().bind(Bindings.createDoubleBinding(() -> Math.max(Math.max(width.get(), maxWidthOfElements.get()), 400), width, maxWidthOfElements));
        root.maxWidthProperty().bind(Bindings.createDoubleBinding(() -> Math.max(Math.max(width.get(), maxWidthOfElements.get()), 400), width, maxWidthOfElements));
        root.minHeightProperty().bind(Bindings.createDoubleBinding(() -> Math.max(Math.max(height.get(), maxHeightOfElements.get()), 400), height, maxHeightOfElements));
        root.maxHeightProperty().bind(Bindings.createDoubleBinding(() -> Math.max(Math.max(height.get(), maxHeightOfElements.get()), 400), height, maxHeightOfElements));
    }
}
