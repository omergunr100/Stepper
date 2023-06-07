package com.main.stepper.statistics;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.statistics.dto.StatDTO;
import com.main.stepper.step.definition.api.StepResult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StatManager implements Serializable {
    private final ObservableList<IFlowRunResult> flowRunResults;
    private final ObservableList<IStepRunResult> stepRunResults;
    private final ObservableList<StatDTO> flowStatDTOs;
    private final ObservableList<StatDTO> stepStatDTOs;

    public StatManager(){
        flowRunResults = FXCollections.observableList(new LinkedList<>());
        stepRunResults = FXCollections.observableList(new LinkedList<>());
        flowStatDTOs = FXCollections.observableArrayList(new ArrayList<>());
        stepStatDTOs = FXCollections.observableArrayList(new ArrayList<>());
    }

    public List<IFlowRunResult> getFlowRuns(){
        return flowRunResults;
    }

    public List<IStepRunResult> getStepRuns(){
        return stepRunResults;
    }

    public void addRunResult(IFlowRunResult flowRunResult){
        flowRunResults.add(0, flowRunResult);
        if(flowRunResult.result().equals(FlowResult.FAILURE))
            return;
        StatDTO dto = null;
        if (flowStatDTOs.stream().noneMatch(statDTO -> statDTO.getName().equals(flowRunResult.name()))) {
            dto = new StatDTO(StatDTO.TYPE.FLOW, flowRunResult.name());
            dto.setRunCounter(getFlowRunCount(flowRunResult.name()));
            dto.setAvgRunTime(getFlowRunAverageTimeMS(flowRunResult.name()));
            final StatDTO finalDTO = dto;
            Platform.runLater(() -> flowStatDTOs.add(finalDTO));
        }
        else {
            synchronized (flowStatDTOs){
                dto = flowStatDTOs.stream().filter(statDTO -> statDTO.getName().equals(flowRunResult.name())).findFirst().get();
                dto.setRunCounter(getFlowRunCount(flowRunResult.name()));
                dto.setAvgRunTime(getFlowRunAverageTimeMS(flowRunResult.name()));
                final StatDTO finalDTO = dto;
                Platform.runLater(() -> flowStatDTOs.set(flowStatDTOs.indexOf(finalDTO), finalDTO));
            }
        }
    }

    public void addRunResult(IStepRunResult stepRunResult){
        stepRunResults.add(0, stepRunResult);
        if(stepRunResult.result().equals(StepResult.FAILURE))
            return;
        StatDTO dto = null;
        if (stepStatDTOs.stream().noneMatch(statDTO -> statDTO.getName().equals(stepRunResult.name()))) {
            dto = new StatDTO(StatDTO.TYPE.STEP, stepRunResult.name());
            synchronized (stepStatDTOs) {
                dto.setRunCounter(getStepRunCount(stepRunResult.name()));
                dto.setAvgRunTime(getStepRunAverageTimeMS(stepRunResult.name()));
                final StatDTO finalDTO = dto;
                Platform.runLater(() -> stepStatDTOs.add(finalDTO));
            }
        }
        else {
            synchronized (stepStatDTOs) {
                dto = stepStatDTOs.stream().filter(statDTO -> statDTO.getName().equals(stepRunResult.name())).findFirst().get();
                dto.setRunCounter(getStepRunCount(stepRunResult.name()));
                dto.setAvgRunTime(getStepRunAverageTimeMS(stepRunResult.name()));
                final StatDTO finalDTO = dto;
                Platform.runLater(() -> stepStatDTOs.set(stepStatDTOs.indexOf(finalDTO), finalDTO));
            }
        }
    }

    public IFlowRunResult getFlowRunResult(String uuid){
        return flowRunResults.stream().filter(f->f.runId().equals(uuid)).findFirst().orElse(null);
    }

    public IStepRunResult getStepRunResult(String uuid){
        return stepRunResults.stream().filter(f->f.runId().equals(uuid)).findFirst().orElse(null);
    }

    public Duration getFlowRunAverageTimeMS(String flowName){
        List<IFlowRunResult> flowRunResults = this.flowRunResults.stream().filter(f->f.name().equals(flowName) && !f.result().equals(FlowResult.FAILURE)).collect(Collectors.toList());
        if(flowRunResults.isEmpty())
            return Duration.ZERO;
        return Duration.ofMillis((long)flowRunResults.stream().map(f->f.duration()).mapToLong(Duration::toMillis).average().orElse(0));

    }

    public Duration getStepRunAverageTimeMS(String stepName){
        List<IStepRunResult> stepRunResults = this.stepRunResults.stream().filter(f->f.name().equals(stepName) && !f.result().equals(StepResult.FAILURE)).collect(Collectors.toList());
        if(stepRunResults.isEmpty())
            return Duration.ZERO;
        return Duration.ofMillis((long)stepRunResults.stream().map(f->f.duration()).mapToLong(Duration::toMillis).average().orElse(0));
    }

    public Integer getFlowRunCount(String flowName){
        return flowRunResults.stream().filter(f->f.name().equals(flowName) && !f.result().equals(FlowResult.FAILURE)).collect(Collectors.toList()).size();
    }

    public Integer getStepRunCount(String stepName){
        return stepRunResults.stream().filter(f->f.name().equals(stepName) && !f.result().equals(StepResult.FAILURE)).collect(Collectors.toList()).size();
    }

    public void clear() {
        flowRunResults.clear();
        flowStatDTOs.clear();
        stepRunResults.clear();
        stepStatDTOs.clear();
    }

    public ObservableList<StatDTO> getStatistics(StatDTO.TYPE type) {
        if(type.equals(StatDTO.TYPE.FLOW))
            return flowStatDTOs;
        return stepStatDTOs;
    }
}
