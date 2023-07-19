package com.main.stepper.admin.resources.fxml.tabs.users.edit;

import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;

public class FlowRunCounter {
    public SimpleBooleanProperty enabled = new SimpleBooleanProperty(false);
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleStringProperty description = new SimpleStringProperty();
    public SimpleIntegerProperty timesRun = new SimpleIntegerProperty();

    public FlowRunCounter(String name, String description, int timesRun) {
        this.name.set(name);
        this.description.set(description);
        this.timesRun.set(timesRun);
    }

    public void setListForEnabled(ObservableList<FlowInfoDTO> flowInfoList) {
        flowInfoList.addListener((ListChangeListener<FlowInfoDTO>) c -> {
            c.next();

            ObservableList<? extends FlowInfoDTO> list = c.getList();
            synchronized (list) {
                if (list.stream().anyMatch(flow -> flow.name().equals(name.get()))) {
                    this.enabled.set(true);
                } else {
                    this.enabled.set(false);
                }
            }
        });
    }

    public void setListForCount(ObservableList<FlowRunResultDTO> flowRunResultList) {
        flowRunResultList.addListener((ListChangeListener<FlowRunResultDTO>) c -> {
            c.next();

            List<? extends FlowRunResultDTO> added = c.getAddedSubList();
            this.timesRun.set((int) (this.timesRun.get() + added.stream().filter(result -> result.name().equals(name.get())).count()));

            List<? extends FlowRunResultDTO> removed = c.getRemoved();
            this.timesRun.set((int) (this.timesRun.get() + removed.stream().filter(result -> result.name().equals(name.get())).count()));
        });
    }
}
