package com.main.stepper.client.resources.fxml.tabs.flowsdefinition;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.client.resources.fxml.reusable.flowdetails.FlowTreeViewController;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.roles.Role;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlowsDefinitionController {
    @FXML private TableView<FlowInfoDTO> flowsTableView;
    @FXML private FlowTreeViewController selectedFlowTreeController;
    @FXML private Button executeFlowButton;

    public FlowsDefinitionController() {
    }

    @FXML public void initialize(){
        // Initialize flow table
        TableColumn<FlowInfoDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));
        nameColumn.setPrefWidth(100);


        TableColumn<FlowInfoDTO, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().description()));
        descriptionColumn.setPrefWidth(200);

        TableColumn<FlowInfoDTO, String> numberOfStepsColumn = new TableColumn<>("Number of steps");
        numberOfStepsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().steps().size())));
        numberOfStepsColumn.setPrefWidth(150);

        TableColumn<FlowInfoDTO, String> numberOfFreeInputsColumn = new TableColumn<>("Number of free inputs");
        numberOfFreeInputsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().openUserInputs().size())));
        numberOfFreeInputsColumn.setPrefWidth(150);

        TableColumn<FlowInfoDTO, String> numberOfContinuationsColumn = new TableColumn<>("Number of continuations");
        numberOfContinuationsColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().continuations().size())));
        numberOfContinuationsColumn.setPrefWidth(150);

        flowsTableView.getColumns().addAll(nameColumn, descriptionColumn, numberOfStepsColumn, numberOfFreeInputsColumn, numberOfContinuationsColumn);

        flowsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            PropertiesManager.currentFlow.set(newValue);
            this.executeFlowButton.setDisable(newValue == null);
        });

        flowsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // listen on available flows to update
        PropertiesManager.flowInformationList.addListener((ListChangeListener<FlowInfoDTO>) c -> {
            while (c.next()) {
                c.getRemoved().forEach(flowInformation -> {
                    if (flowInformation.equals(PropertiesManager.currentFlow.get()))
                        PropertiesManager.currentFlow.set(null);
                    flowsTableView.getItems().remove(flowInformation);
                });
                // todo: filter based on selection
                List<FlowInfoDTO> toAdd = new ArrayList<>();
                c.getAddedSubList().forEach(dto -> {
                    if (PropertiesManager.roleFilter.get().equals("") || (!PropertiesManager.roles.isEmpty() && PropertiesManager.roles.stream().filter(r -> r.name().equals(PropertiesManager.roleFilter.get())).findFirst().get().allowedFlows().contains(dto.name())))
                        toAdd.add(dto);
                });
                flowsTableView.getItems().addAll(toAdd);
            }
        });

        // listen on role filter change to update flow table
        PropertiesManager.roleFilter.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                // no filter
                List<FlowInfoDTO> toRemove = new ArrayList<>();
                flowsTableView.getItems().forEach(dto -> {
                    if (!PropertiesManager.flowInformationList.contains(dto))
                        toRemove.add(dto);
                });
                if (toRemove.contains(PropertiesManager.currentFlow.get()))
                    PropertiesManager.currentFlow.set(null);

                flowsTableView.getItems().removeAll(toRemove);

                PropertiesManager.flowInformationList.forEach(flowInfoDTO -> {
                    if (!flowsTableView.getItems().contains(flowInfoDTO))
                        flowsTableView.getItems().add(flowInfoDTO);
                });
            }
            else {
                // filter based on role string
                Optional<Role> first;
                synchronized (PropertiesManager.roles) {
                    first = PropertiesManager.roles.stream().filter(r -> r.name().equals(newValue)).findFirst();
                }
                if (first.isPresent()) {
                    List<String> allowedNames = first.get().allowedFlows();
                    List<FlowInfoDTO> allowedFlows = PropertiesManager.flowInformationList.stream().filter(flowInfoDTO -> allowedNames.contains(flowInfoDTO.name())).collect(Collectors.toList());

                    ObservableList<FlowInfoDTO> tableItems = flowsTableView.getItems();
                    List<FlowInfoDTO> toRemove = new ArrayList<>();
                    tableItems.stream().filter(dto -> !allowedFlows.contains(dto)).forEach(dto -> {
                        if (dto.equals(PropertiesManager.currentFlow.get()))
                            PropertiesManager.currentFlow.set(null);
                        toRemove.add(dto);
                    });
                    tableItems.removeAll(toRemove);
                    allowedFlows.stream().filter(dto -> !tableItems.contains(dto)).forEach(tableItems::add);
                }
                else {
                    new ErrorPopup("Role not found, please wait for data to update.");
                }
            }
        });
    }

    public void executeFlow() {
        PropertiesManager.executionSelectedFlow.set(null);
        PropertiesManager.executionSelectedFlow.set(PropertiesManager.currentFlow.get());
    }
}
