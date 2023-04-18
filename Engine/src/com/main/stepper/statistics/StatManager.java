package com.main.stepper.statistics;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class StatManager {
    public enum TYPE{FLOW, STEP};
    private HashMap<String, List<Duration>> stepRuns;
    private HashMap<String, List<Duration>> flowRuns;

    public void remove(TYPE type, String name){
        if(type == TYPE.FLOW){
            flowRuns.remove(name);
        }else{
            stepRuns.remove(name);
        }
    }
    public void add(TYPE type, String name, Duration time){
        if(type == TYPE.FLOW){
            if(!flowRuns.containsKey(name))
                flowRuns.put(name, new ArrayList<>());
            flowRuns.get(name).add(time);
        }else{
            if(!stepRuns.containsKey(name))
                stepRuns.put(name, new ArrayList<>());
            stepRuns.get(name).add(time);
        }
    }
    public int getTimesRun(TYPE type, String name){
        if(type == TYPE.FLOW){
            return flowRuns.get(name).size();
        }else{
            return stepRuns.get(name).size();
        }
    }
    public Long getAverageTime(TYPE type, String name){
        Optional<List<Duration>> list;
        if(type == TYPE.FLOW)
            list = Optional.ofNullable(flowRuns.get(name));
        else
            list = Optional.ofNullable(stepRuns.get(name));

        if(!list.isPresent())
            return null;

        long sum = list.get().stream().mapToLong(Duration::toMillis).sum();
        int count = list.get().size();

        if(count > 0)
            return sum/count;
        return null;
    }

    private StatManager(){
        stepRuns = new HashMap<>();
        flowRuns = new HashMap<>();
    }
}
