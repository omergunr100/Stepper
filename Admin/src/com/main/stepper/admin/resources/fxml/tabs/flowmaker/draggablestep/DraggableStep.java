package com.main.stepper.admin.resources.fxml.tabs.flowmaker.draggablestep;

import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import com.main.stepper.shared.structures.step.StepUsageDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DraggableStep extends VBox {
    // data
    private final StepDefinitionDTO step;
    private final SimpleBooleanProperty isSkipIfFailed;
    private final SimpleStringProperty alias;
    private final SimpleBooleanProperty isReadOnly;

    // ui components
    private HBox aliasContainer; // contains alias textfield
    private HBox skipIfFailedContainer; // contains skip if failed checkbox
    private HBox stepNameContainer; // contains step name, on hover shows step description
    private HBox isReadOnlyContainer; // contains is read only checkbox


    public DraggableStep(StepDefinitionDTO step) {
        // setup root
        super();
        this.setSpacing(10);

        this.step = step;
        this.alias = new SimpleStringProperty(step.name());
        this.isSkipIfFailed = new SimpleBooleanProperty(false);
        this.isReadOnly = new SimpleBooleanProperty(step.isReadOnly());

        initialize();
    }

    private void initialize() {
        // setup alias container
        aliasContainer = new HBox();
        aliasContainer.setSpacing(10);
        Label aliasLabel = new Label("Alias:");
        TextField aliasTextField = new TextField();
        aliasTextField.textProperty().bindBidirectional(alias);
        aliasContainer.getChildren().addAll(aliasLabel, aliasTextField);
        this.getChildren().add(aliasContainer);

        // setup skip if failed container
        skipIfFailedContainer = new HBox();
        skipIfFailedContainer.setSpacing(10);
        Label skipIfFailedLabel = new Label("Skip if failed:");
        CheckBox skipIfFailedCheckBox = new CheckBox();
        skipIfFailedCheckBox.selectedProperty().bindBidirectional(isSkipIfFailed);
        skipIfFailedContainer.getChildren().addAll(skipIfFailedLabel, skipIfFailedCheckBox);
        this.getChildren().add(skipIfFailedContainer);

        // setup step name container
        stepNameContainer = new HBox();
        stepNameContainer.setSpacing(10);
        Label stepNameLabel = new Label("Step name:");
        Label stepName = new Label(step.name());
        stepNameContainer.getChildren().addAll(stepNameLabel, stepName);
        this.getChildren().add(stepNameContainer);

        // setup is read only container
        isReadOnlyContainer = new HBox();
        isReadOnlyContainer.setSpacing(10);
        Label isReadOnlyLabel = new Label("Is read only:");
        CheckBox isReadOnlyCheckBox = new CheckBox();
        isReadOnlyCheckBox.selectedProperty().bind(isReadOnly);
        isReadOnlyContainer.getChildren().addAll(isReadOnlyLabel, isReadOnlyCheckBox);
        this.getChildren().add(isReadOnlyContainer);
    }

    public StepUsageDTO usage() {
        return new StepUsageDTO(alias.get(), step, isSkipIfFailed.get());
    }

    public StepDefinitionDTO step() {
        return step;
    }
}
