package com.main.stepper.admin.resources.fxml.tabs.flowmaker.stepproperties;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview.datalink.DataLink;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.draggablestep.DraggableStep;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StepPropertiesController {
    @FXML private Text stepText;
    @FXML private TextField aliasTextField;
    @FXML private CheckBox readOnlyBox;
    @FXML private CheckBox skipIfFailedBox;

    @FXML private TableView<DataLink> dataIOTable;
    @FXML private TableColumn<DataLink, String> nameCol;
    @FXML private TableColumn<DataLink, String> aliasCol;
    @FXML private TableColumn<DataLink, String> necessityCol;
    @FXML private TableColumn<DataLink, String> typeCol;
    @FXML private TableColumn<DataLink, Boolean> hasInitialValueCol;
    @FXML private TableColumn<DataLink, String> initialValueCol;

    public StepPropertiesController() {
    }

    @FXML public void initialize() {
        // setup data table
        dataIOTable.setPlaceholder(new Text("No data inputs or outputs"));
        setupColumns();

        PropertiesManager.selectedDraggableStepForProperties.addListener((observable, oldValue, newValue) -> {
            reset(oldValue);
            if (newValue != null)
                setStep(newValue);
        });
    }

    private void setupColumns() {
        nameCol.setCellValueFactory(param -> param.getValue().nameProperty);

        aliasCol.setCellValueFactory(param -> param.getValue().aliasProperty);
        aliasCol.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));

        necessityCol.setCellValueFactory(param -> Bindings.createStringBinding(() -> {
            switch (param.getValue().necessityProperty.get()) {
                case MANDATORY:
                    return "Input - Mandatory";
                case OPTIONAL:
                    return "Input - Optional";
                case NA:
                    return "Output";
                default:
                    return "Error";
            }
        }));

        typeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().typeProperty.get().getName()));

        hasInitialValueCol.setCellValueFactory(param -> param.getValue().hasInitialValueProperty);
        hasInitialValueCol.setCellFactory(param -> {
            TableCell<DataLink, Boolean> cell = new CheckBoxTableCell<>();
            Platform.runLater(() -> {
                ObjectProperty itemProperty = cell.getTableRow().itemProperty();
                itemProperty.addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        DataLink item = (DataLink) newValue;
                        if (!item.typeProperty.get().isUserFriendly() || item.necessityProperty.get().equals(DataNecessity.NA)){
                            item.hasInitialValueProperty.addListener((observable1, oldValue1, newValue1) -> {
                                if (newValue1)
                                    item.hasInitialValueProperty.set(false);
                            });
                        }

                        Tooltip.uninstall(cell, cell.getTooltip());
                        if (!item.typeProperty.get().isUserFriendly())
                            Tooltip.install(cell, new Tooltip("Only user friendly types can have initial values"));
                        else if (item.necessityProperty.get().equals(DataNecessity.NA))
                            Tooltip.install(cell, new Tooltip("Output data cannot have initial values"));
                    }
                });
                if (itemProperty.isNotNull().get()) {
                    DataLink item = (DataLink) itemProperty.get();
                    if (!item.typeProperty.get().isUserFriendly() || item.necessityProperty.get().equals(DataNecessity.NA)){
                        item.hasInitialValueProperty.addListener((observable1, oldValue1, newValue1) -> {
                            if (newValue1)
                                item.hasInitialValueProperty.set(false);
                        });
                    }

                    Tooltip.uninstall(cell, cell.getTooltip());
                    if (item.necessityProperty.get().equals(DataNecessity.NA))
                        Tooltip.install(cell, new Tooltip("Output data cannot have initial values"));
                    else if (!item.typeProperty.get().isUserFriendly())
                        Tooltip.install(cell, new Tooltip("Only user friendly types can have initial values"));
                }
            });
            return cell;
        });

        initialValueCol.setCellValueFactory(param -> param.getValue().initialValueProperty);
        initialValueCol.setCellFactory(param -> {
            TableCell<DataLink, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());
            Platform.runLater(() -> {
                ObjectProperty itemProperty = cell.getTableRow().itemProperty();
                itemProperty.addListener((observable, oldValue, newValue) -> {
                    if (itemProperty.isNotNull().get()) {
                        DataLink item = (DataLink) itemProperty.get();
                        cell.editableProperty().bind(item.hasInitialValueProperty);
                        cell.disableProperty().bind(cell.editableProperty().not());
                        item.hasInitialValueProperty.addListener((observable1, oldValue1, newValue1) -> {
                            if (!newValue1) {
                                item.prevInitialValueProperty.set(cell.getText());
                                cell.setText("");
                            }
                            else
                                cell.setText(item.prevInitialValueProperty.get());
                        });
                        if (!item.hasInitialValueProperty.get()) {
                            item.prevInitialValueProperty.set(cell.getText());
                            cell.setText("");
                        }
                        else
                            cell.setText(item.prevInitialValueProperty.get());
                    }
                });
            });
            return cell;
        });
    }

    public void reset(DraggableStep old) {
        // clear header
        if (old != null) {
            stepText.textProperty().set("");

            aliasTextField.textProperty().unbindBidirectional(old.aliasProperty());

            readOnlyBox.selectedProperty().unbind();

            skipIfFailedBox.selectedProperty().unbindBidirectional(old.isSkipIfFailedProperty());
        }

        // clear table
        dataIOTable.getItems().clear();
    }

    private void setStep(DraggableStep step) {
        // set header
        stepText.textProperty().set(step.step().name());

        aliasTextField.textProperty().bindBidirectional(step.aliasProperty());

        readOnlyBox.selectedProperty().bind(step.isReadOnlyProperty());
        readOnlyBox.setDisable(true);
        readOnlyBox.setStyle("-fx-opacity: 1");

        skipIfFailedBox.selectedProperty().bindBidirectional(step.isSkipIfFailedProperty());

        // set table
        ObservableMap<DataIODTO, DataLink> map = PropertiesManager.stepToDataLinkMap.get(step);
        List<DataLink> dataLinks = map.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        dataIOTable.getItems().setAll(dataLinks);
    }
}
