package com.main.stepper.admin.resources.fxml.tabs.users.edit;

import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class FlowRunCounter {
    public SimpleBooleanProperty enabled = new SimpleBooleanProperty(false);
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleStringProperty description = new SimpleStringProperty();
    public SimpleIntegerProperty timesRun = new SimpleIntegerProperty();

    public FlowRunCounter(FlowInfoDTO flowInfoDTO) {
        this(flowInfoDTO.name(), flowInfoDTO.description(), false, 0);
    }

    public FlowRunCounter(String name, String description, Boolean isEnabled, int timesRun) {
        this.name.set(name);
        this.description.set(description);
        this.enabled.set(isEnabled);
        this.timesRun.set(timesRun);
    }

    public void listForSelected(ObservableList<String> list) {
        list.addListener((ListChangeListener<String>) c -> {
            c.next();
            if (c.getList().contains(name.get())) {
                enabled.set(true);
            } else {
                enabled.set(false);
            }
        });
    }
}
