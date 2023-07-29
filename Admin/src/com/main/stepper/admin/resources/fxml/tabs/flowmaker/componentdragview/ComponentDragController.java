package com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.draggablestep.DraggableStep;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.main.stepper.admin.resources.data.PropertiesManager.stepDefinitions;

public class ComponentDragController {
    @FXML private VBox root;

    private List<DraggableStep> stepComponents;

    public ComponentDragController() {
    }

    @FXML public void initialize() {
        // initialize step list
        stepComponents = new ArrayList<>();

        // add listener on stepDefinitions
        stepDefinitions.addListener((ListChangeListener<? super StepDefinitionDTO>) c -> {
            while (c.next()) {
                if (!stepComponents.isEmpty()) {
                    List<DraggableStep> toRemove = stepComponents.stream().filter(comp -> c.getRemoved().contains(comp.step())).collect(Collectors.toList());
                    root.getChildren().removeIf(toRemove::contains);
                    stepComponents.removeIf(toRemove::contains);
                }

                if (!c.getAddedSubList().isEmpty()) {
                    c.getAddedSubList().forEach(step -> {
                        DraggableStep dStep = new DraggableStep(step);
                        root.getChildren().add(dStep);
                    });
                }
            }
        });
    }
}
