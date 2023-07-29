package com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview.datalink;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InitialValueDescriptor {
    private final SimpleStringProperty identifier; // final data name
    private final SimpleStringProperty value; // actual initial value
    private final SimpleBooleanProperty hasOutput; // if there is an output for this initial value (if there is, can't have initial value)
    private final SimpleBooleanProperty isActive;
    private final ObservableList<DataLink> dataLinks; // list of data links that use this value

    public InitialValueDescriptor(String identifier) {
        this.identifier = new SimpleStringProperty(identifier);
        this.value = new SimpleStringProperty("");
        this.hasOutput = new SimpleBooleanProperty(false);
        this.isActive = new SimpleBooleanProperty(false);
        this.dataLinks = FXCollections.observableArrayList();
    }
}
