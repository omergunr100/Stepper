package com.main.stepper.admin.resources.fxml.tabs.statistics;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.fxml.reusable.selector.SelectorController;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import com.main.stepper.statistics.dto.StatDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.main.stepper.admin.resources.data.PropertiesManager.*;

public class StatisticsScreenController {
    @FXML private TableView<StatDTO> flowTable;
    @FXML private TableView<StatDTO> stepTable;
    @FXML private BarChart<String, Number> flowBarChart;
    @FXML private BarChart<String, Number> stepBarChart;
    @FXML private SelectorController userSelectorController;
    private ObservableList<StatDTO> flowStatsList;
    private ObservableList<StatDTO> stepStatsList;
    private XYChart.Series<String, Number> flowTimesRun;
    private XYChart.Series<String, Number> flowAvgRunTime;
    private XYChart.Series<String, Number> stepTimesRun;
    private XYChart.Series<String, Number> stepAvgRunTime;

    public StatisticsScreenController() {
    }

    @FXML public void initialize() {
        // bind user selector
        userSelectorController.setProperty(statisticsSelectedUser);

        // initialize properties
        flowStatsList = flowTable.itemsProperty().get();
        stepStatsList = stepTable.itemsProperty().get();

        // create name columns
        TableColumn<StatDTO, String> flowNameColumn = new TableColumn<>("Flow Name");
        flowNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        flowTable.getColumns().add(flowNameColumn);
        TableColumn<StatDTO, String> stepNameColumn = new TableColumn<>("Step Name");
        stepNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        stepTable.getColumns().add(stepNameColumn);

        // create run counter columns
        TableColumn<StatDTO, String> flowRunCounterColumn = new TableColumn<>("Times Run");
        flowRunCounterColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRunCounter().toString()));
        flowTable.getColumns().add(flowRunCounterColumn);
        TableColumn<StatDTO, String> stepRunCounterColumn = new TableColumn<>("Times Run");
        stepRunCounterColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRunCounter().toString()));
        stepTable.getColumns().add(stepRunCounterColumn);

        // create average run time columns
        TableColumn<StatDTO, String> flowAverageRunTimeColumn = new TableColumn<>("Average Run Time");
        flowAverageRunTimeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAvgRunTime().toMillis() + " ms"));
        flowTable.getColumns().add(flowAverageRunTimeColumn);
        TableColumn<StatDTO, String> stepAverageRunTimeColumn = new TableColumn<>("Average Run Time");
        stepAverageRunTimeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAvgRunTime().toMillis() + " ms"));
        stepTable.getColumns().add(stepAverageRunTimeColumn);

        flowTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stepTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // initialize bar charts
        flowTimesRun = new XYChart.Series<>();
        flowTimesRun.setName("Times run");
        flowAvgRunTime = new XYChart.Series<>();
        flowAvgRunTime.setName("Average Run Time - ms");
        stepTimesRun = new XYChart.Series<>();
        stepTimesRun.setName("Times run");
        stepAvgRunTime = new XYChart.Series<>();
        stepAvgRunTime.setName("Average Run Time - ms");

        flowBarChart.getData().add(flowTimesRun);
        flowBarChart.getData().add(flowAvgRunTime);
        stepBarChart.getData().add(stepTimesRun);
        stepBarChart.getData().add(stepAvgRunTime);

        // set no animations
        flowBarChart.setAnimated(false);
        stepBarChart.setAnimated(false);

        // bind for changes from PropertyManager
        PropertiesManager.flowRunResults.addListener((ListChangeListener.Change<? extends FlowRunResultDTO> c) -> {
            onFlowRunResultsUpdate();
        });
        PropertiesManager.stepRunResults.addListener((ListChangeListener.Change<? extends StepRunResultDTO> c) -> {
            onStepRunResultsUpdate();
        });

        statisticsSelectedUser.addListener((observable, oldValue, newValue) -> {
            onFlowRunResultsUpdate();
            onStepRunResultsUpdate();
        });
    }

    private void onFlowRunResultsUpdate() {
        // collect all unique flow run names
        Set<String> uniqueFlowNames;
        synchronized(PropertiesManager.flowRunResults) {
            uniqueFlowNames = PropertiesManager.flowRunResults.stream().filter(f -> !f.result().equals(FlowResult.RUNNING)).map(FlowRunResultDTO::name).collect(Collectors.toSet());
        }
        // for each unique flow name, create a new StatDTO from data in flowRunResults
        synchronized (PropertiesManager.flowRunResults) {
            for (String name : uniqueFlowNames) {
                // calculate new statDTO
                List<FlowRunResultDTO> collection = PropertiesManager.flowRunResults.stream().filter(f -> (statisticsSelectedUser.get().equals("") ? true : f.user().equals(statisticsSelectedUser.get())) && f.name().equals(name) && !f.result().equals(FlowResult.RUNNING)).collect(Collectors.toList());
                Duration duration = Duration.ofMillis(collection.stream().mapToLong(f -> f.duration().toMillis()).sum() / collection.size());
                StatDTO statDTO = new StatDTO(StatDTO.TYPE.FLOW, name, collection.size(), duration);

                // check if statDTO already exists in list
                Optional<StatDTO> first;
                synchronized (filteredFlowStatDTOs) {
                    first = filteredFlowStatDTOs.stream().filter(s -> s.getName().equals(statDTO.getName())).findFirst();
                }
                if (first.isPresent()) {
                    // if it does, update the existing statDTO
                    StatDTO existingStatDTO = first.get();
                    existingStatDTO.setRunCounter(statDTO.getRunCounter());
                    existingStatDTO.setAvgRunTime(statDTO.getAvgRunTime());
                } else {
                    // if it doesn't, add it to the list
                    filteredFlowStatDTOs.add(statDTO);
                }
            }
        }
        // update chart
        flowTimesRun.getData().clear();
        flowAvgRunTime.getData().clear();
        if(!filteredFlowStatDTOs.isEmpty()) {
            synchronized (filteredFlowStatDTOs) {
                flowTimesRun.getData().addAll(filteredFlowStatDTOs.stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getRunCounter())).collect(Collectors.toList()));
                flowAvgRunTime.getData().addAll(filteredFlowStatDTOs.stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getAvgRunTime().toMillis())).collect(Collectors.toList()));
            }
        }
        // update table
        flowStatsList.setAll(filteredFlowStatDTOs);
        flowTable.refresh();
    }

    private void onStepRunResultsUpdate() {
        // collect all unique step run names
        Set<String> uniqueStepNames;
        synchronized(PropertiesManager.stepRunResults) {
            uniqueStepNames = PropertiesManager.stepRunResults.stream().map(StepRunResultDTO::name).collect(Collectors.toSet());
        }
        // for each unique step name, create a new StatDTO from data in stepRunResults
        synchronized (PropertiesManager.stepRunResults) {
            for (String name : uniqueStepNames) {
                // calculate new statDTO
                List<StepRunResultDTO> collection = PropertiesManager.stepRunResults.stream().filter(s -> (statisticsSelectedUser.get().equals("") ? true : s.user().equals(statisticsSelectedUser.get())) && s.name().equals(name)).collect(Collectors.toList());
                Duration duration = Duration.ofMillis(collection.stream().mapToLong(s -> s.duration().toMillis()).sum() / collection.size());
                StatDTO statDTO = new StatDTO(StatDTO.TYPE.STEP, name, collection.size(), duration);

                // check if statDTO already exists in list
                Optional<StatDTO> first;
                synchronized (filteredStepStatDTOs) {
                    first = filteredStepStatDTOs.stream().filter(s -> s.getName().equals(statDTO.getName())).findFirst();
                }
                if (first.isPresent()) {
                    // if it does, update the existing statDTO
                    StatDTO existingStatDTO = first.get();
                    existingStatDTO.setRunCounter(statDTO.getRunCounter());
                    existingStatDTO.setAvgRunTime(statDTO.getAvgRunTime());
                } else {
                    // if it doesn't, add it to the list
                    filteredStepStatDTOs.add(statDTO);
                }
            }
        }
        // update chart
        stepTimesRun.getData().clear();
        stepAvgRunTime.getData().clear();
        if(!stepStatsList.isEmpty()) {
            synchronized (stepStatsList) {
                stepTimesRun.getData().addAll(stepStatsList.stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getRunCounter())).collect(Collectors.toList()));
                stepAvgRunTime.getData().addAll(stepStatsList.stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getAvgRunTime().toMillis())).collect(Collectors.toList()));
            }
        }
        // update table
        stepStatsList.setAll(filteredStepStatDTOs);
        stepTable.refresh();
    }
}
