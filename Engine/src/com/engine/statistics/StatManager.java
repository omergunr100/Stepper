package com.engine.statistics;

import java.util.HashMap;
import java.util.List;

public class StatManager {
    public enum TYPE{FLOW, STEP};
    private HashMap<String, List<Long>> stepRuns;
    private HashMap<String, List<Long>> flowRuns;

    private static StatManager instance;
    public void init(){
        instance = new StatManager();
    }
    public static void remove(TYPE type, String name){
        if(type == TYPE.FLOW){
            instance.flowRuns.remove(name);
        }else{
            instance.stepRuns.remove(name);
        }
    }
    public static void add(TYPE type, String name, long time){
        if(type == TYPE.FLOW){
            instance.flowRuns.get(name).add(time);
        }else{
            instance.stepRuns.get(name).add(time);
        }
    }
    public static int getTimesRun(TYPE type, String name){
        if(type == TYPE.FLOW){
            return instance.flowRuns.get(name).size();
        }else{
            return instance.stepRuns.get(name).size();
        }
    }
    public static long getAverageTime(TYPE type, String name){
        long sum;
        int count;
        if(type == TYPE.FLOW){
            sum = instance.flowRuns.get(name).stream().mapToLong(Long::longValue).sum();
            count = instance.flowRuns.get(name).size();
        }else{
            sum = instance.stepRuns.get(name).stream().mapToLong(Long::longValue).sum();
            count = instance.stepRuns.get(name).size();
        }
        if(count > 0)
            return sum/count;
        return -1;
    }

    private StatManager(){
        stepRuns = new HashMap<>();
        flowRuns = new HashMap<>();
    }
}
