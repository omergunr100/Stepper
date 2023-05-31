package com.main.stepper.statistics.dto;

import java.time.Duration;
import java.util.Objects;

public class StatDTO {
    public enum TYPE {FLOW, STEP}
    private TYPE type;
    private String name;
    private Integer runCounter;
    private Duration avgRunTime;

    public StatDTO(TYPE type, String name) {
        this.type = type;
        this.name = name;
        this.runCounter = 0;
        this.avgRunTime = Duration.ZERO;
    }
    public StatDTO(TYPE type, String name, Integer runCounter, Duration avgRunTime) {
        this.type = type;
        this.name = name;
        this.runCounter = runCounter;
        this.avgRunTime = avgRunTime;
    }

    public TYPE getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setRunCounter(Integer runCounter) {
        this.runCounter = runCounter;
    }
    public Integer getRunCounter() {
        return runCounter;
    }

    public void setAvgRunTime(Duration avgRunTime) {
        this.avgRunTime = avgRunTime;
    }
    public Duration getAvgRunTime() {
        return avgRunTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatDTO dto = (StatDTO) o;

        if (type != dto.type) return false;
        return Objects.equals(name, dto.name);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
