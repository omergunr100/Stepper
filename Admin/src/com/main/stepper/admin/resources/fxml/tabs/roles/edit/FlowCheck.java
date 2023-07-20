package com.main.stepper.admin.resources.fxml.tabs.roles.edit;

import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Optional;

public class FlowCheck {
    public SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleStringProperty description = new SimpleStringProperty();
    private FlowInfoDTO flowInfoDTO;

    public FlowCheck(FlowInfoDTO flow, Boolean selected) {
        this.flowInfoDTO = flow;
        this.name.set(flow.name());
        this.description.set(flow.description());
        this.selected.set(selected);
    }

    public void listForSelected(ObservableList<FlowInfoDTO> userRolesList) {
        userRolesList.addListener((ListChangeListener<FlowInfoDTO>) c -> {
            while(c.next()) {
                ObservableList<? extends FlowInfoDTO> list = c.getList();
                synchronized (list) {
                    Optional<? extends FlowInfoDTO> first = list.stream().filter(role -> role.name().equals(name.get())).findFirst();
                    if (first.isPresent())
                        selected.set(true);
                    else
                        selected.set(false);
                }
            }
        });
    }

    public FlowInfoDTO flowInfoDTO() {
        return flowInfoDTO;
    }
}
