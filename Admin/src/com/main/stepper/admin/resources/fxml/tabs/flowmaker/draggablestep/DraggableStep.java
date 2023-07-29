package com.main.stepper.admin.resources.fxml.tabs.flowmaker.draggablestep;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import com.main.stepper.shared.structures.step.StepUsageDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static com.main.stepper.admin.resources.data.PropertiesManager.selectedDraggableStepForProperties;

public class DraggableStep extends VBox {
    // data
    private final StepDefinitionDTO step;
    private final SimpleBooleanProperty isSkipIfFailed;
    private final SimpleStringProperty alias;
    private final SimpleBooleanProperty isReadOnly;

    // ui property
    private final SimpleBooleanProperty isInFlow;
    private final SimpleBooleanProperty delete;

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

        this.isInFlow = new SimpleBooleanProperty(false);
        this.delete = new SimpleBooleanProperty(false);

        this.setOnMouseClicked(event -> {
            if (isInFlow.not().get() && event.getButton().equals(MouseButton.PRIMARY)) {
                onPrimaryMouse();
            }
            else if (isInFlow.get() && event.getButton().equals(MouseButton.PRIMARY)) {
                selectForProperties();
            }
        });

        this.setOnContextMenuRequested(event -> {
            if (isInFlow.get())
                onContextMenuRequested(event.getScreenX(), event.getScreenY());
        });

        initialize();
    }

    private void selectForProperties() {
        selectedDraggableStepForProperties.set(this);
    }

    private void initialize() {
        // setup alias container
        aliasContainer = new HBox();
        aliasContainer.setSpacing(10);
        Text aliasText = new Text("Alias:");
        TextField aliasTextField = new TextField();
        aliasTextField.editableProperty().bind(isInFlow);
        aliasTextField.textProperty().bindBidirectional(alias);
        aliasContainer.getChildren().addAll(aliasText, aliasTextField);
        this.getChildren().add(aliasContainer);

        // setup skip if failed container
        skipIfFailedContainer = new HBox();
        skipIfFailedContainer.setSpacing(10);
        Text skipIfFailedLabel = new Text("Skip if failed:");
        CheckBox skipIfFailedCheckBox = new CheckBox();
        skipIfFailedCheckBox.setDisable(true);
        skipIfFailedCheckBox.setStyle("-fx-opacity: 1");
        isInFlow.addListener((observable, oldValue, newValue) -> {
            skipIfFailedCheckBox.setDisable(!newValue);
            skipIfFailedCheckBox.setStyle("-fx-opacity: 1");
        });
        skipIfFailedCheckBox.selectedProperty().bindBidirectional(isSkipIfFailed);
        skipIfFailedContainer.getChildren().addAll(skipIfFailedLabel, skipIfFailedCheckBox);
        this.getChildren().add(skipIfFailedContainer);

        // setup step name container
        stepNameContainer = new HBox();
        stepNameContainer.setSpacing(10);
        Text stepNameLabel = new Text("Step name:");
        Text stepName = new Text(step.name());
        stepNameContainer.getChildren().addAll(stepNameLabel, stepName);
        this.getChildren().add(stepNameContainer);

        // setup is read only container
        isReadOnlyContainer = new HBox();
        isReadOnlyContainer.setSpacing(10);
        Text isReadOnlyLabel = new Text("Is read only:");
        CheckBox isReadOnlyCheckBox = new CheckBox();
        isReadOnlyCheckBox.selectedProperty().bind(isReadOnly);
        isReadOnlyCheckBox.setDisable(true);
        isReadOnlyCheckBox.setStyle("-fx-opacity: 1");
        isReadOnlyContainer.getChildren().addAll(isReadOnlyLabel, isReadOnlyCheckBox);
        this.getChildren().add(isReadOnlyContainer);

        this.setStyle("-fx-border-color: #000000; -fx-border-width: 2px; -fx-border-style: solid;");
    }

    public StepUsageDTO usage() {
        return new StepUsageDTO(alias.get(), step, isSkipIfFailed.get());
    }

    public StepDefinitionDTO step() {
        return step;
    }

    public void onPrimaryMouse() {
        PropertiesManager.selectedStep.set(step);
    }

    public void onContextMenuRequested(double x, double y) {
        ContextMenu menu = new ContextMenu();

        MenuItem move = new MenuItem("Move");
        move.setOnAction(event -> {
            PropertiesManager.selectedDraggableStepForMoving.set(this);
        });
        menu.getItems().add(move);

        if (selectedDraggableStepForProperties.get() != null && selectedDraggableStepForProperties.get().equals(this)) {
            MenuItem properties = new MenuItem("Deselect for properties");
            properties.setOnAction(event -> {
                PropertiesManager.selectedDraggableStepForProperties.set(null);
            });
            menu.getItems().add(properties);
        }

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            this.delete.set(true);
        });
        menu.getItems().add(delete);


        menu.show(this.getScene().getWindow(), x, y);
    }

    public void setIsInFlow(boolean isInFlow) {
        this.isInFlow.set(isInFlow);
    }

    public SimpleBooleanProperty deleteProperty() {
        return delete;
    }

    public String getAlias() {
        return alias.get();
    }

    public SimpleStringProperty aliasProperty() {
        return alias;
    }

    public SimpleBooleanProperty isSkipIfFailedProperty() {
        return isSkipIfFailed;
    }

    public SimpleBooleanProperty isReadOnlyProperty() {
        return isReadOnly;
    }
}
