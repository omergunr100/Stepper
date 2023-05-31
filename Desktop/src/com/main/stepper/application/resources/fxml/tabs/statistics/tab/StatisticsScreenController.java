package com.main.stepper.application.resources.fxml.tabs.statistics.tab;

import com.main.stepper.application.resources.fxml.tabs.statistics.selector.SelectorController;
import com.main.stepper.statistics.dto.StatDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.stream.Collectors;

public class StatisticsScreenController {
    @FXML private TableView<StatDTO> flowTable;
    @FXML private TableView<StatDTO> stepTable;
    @FXML private BarChart<String, Number> flowBarChart;
    @FXML private BarChart<String, Number> stepBarChart;
    @FXML private SelectorController flowSelectorController;
    @FXML private SelectorController stepSelectorController;
    private Property<ObservableList<StatDTO>> flowStatsProperty;
    private Property<ObservableList<StatDTO>> stepStatsProperty;
    private XYChart.Series<String, Number> flowTimesRun;
    private XYChart.Series<String, Number> flowAvgRunTime;
    private XYChart.Series<String, Number> stepTimesRun;
    private XYChart.Series<String, Number> stepAvgRunTime;

    public StatisticsScreenController() {
    }

    @FXML public void initialize() {
        // initialize properties
        flowStatsProperty = flowTable.itemsProperty();
        stepStatsProperty = stepTable.itemsProperty();

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
    }

    public void bind(ObservableList<StatDTO> flowStats, ObservableList<StatDTO> stepStats) {
        // bind properties
        Bindings.bindContent(flowStatsProperty.getValue(), flowStats);
        Bindings.bindContent(stepStatsProperty.getValue(), stepStats);
        flowStatsProperty.getValue().addListener((ListChangeListener<? super StatDTO>) change -> updateCharts());
        stepStatsProperty.getValue().addListener((ListChangeListener<? super StatDTO>) change -> updateCharts());
        // clear chart data
        flowTimesRun.getData().clear();
        flowAvgRunTime.getData().clear();
        stepTimesRun.getData().clear();
        stepAvgRunTime.getData().clear();
    }

    public void updateCharts() {
        // update tables
        flowTable.refresh();
        stepTable.refresh();

        // update flow charts
        flowTimesRun.getData().clear();
        flowAvgRunTime.getData().clear();
        if(!flowStatsProperty.getValue().isEmpty()) {
            flowTimesRun.getData().addAll(flowStatsProperty.getValue().stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getRunCounter())).collect(Collectors.toList()));
            flowAvgRunTime.getData().addAll(flowStatsProperty.getValue().stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getAvgRunTime().toMillis())).collect(Collectors.toList()));
        }

        // update step charts
        stepTimesRun.getData().clear();
        stepAvgRunTime.getData().clear();
        if(!stepStatsProperty.getValue().isEmpty()) {
            stepTimesRun.getData().addAll(stepStatsProperty.getValue().stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getRunCounter())).collect(Collectors.toList()));
            stepAvgRunTime.getData().addAll(stepStatsProperty.getValue().stream().map(statDTO -> new XYChart.Data<String, Number>(statDTO.getName(), statDTO.getAvgRunTime().toMillis())).collect(Collectors.toList()));
        }
    }
}
