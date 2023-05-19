package com.main.stepper.statistics;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.step.definition.api.StepResult;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StatManager implements Serializable {
    private final List<IFlowRunResult> flowRunResults;
    private final List<IStepRunResult> stepRunResults;

    public StatManager(){
        flowRunResults = Collections.synchronizedList(new LinkedList<>());
        stepRunResults = Collections.synchronizedList(new LinkedList<>());
    }

    public List<IFlowRunResult> getFlowRuns(){
        return flowRunResults;
    }

    public List<IStepRunResult> getStepRuns(){
        return stepRunResults;
    }

    public void addRunResult(IFlowRunResult flowRunResult){
        flowRunResults.add(0, flowRunResult);
    }

    public void addRunResult(IStepRunResult stepRunResult){
        stepRunResults.add(0, stepRunResult);
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
        stepRunResults.clear();
    }
}
