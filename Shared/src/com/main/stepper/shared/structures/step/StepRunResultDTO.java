package com.main.stepper.shared.structures.step;

import com.main.stepper.step.definition.api.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class StepRunResultDTO {
    private UUID runId;
    private String alias;
    private StepDefinitionDTO step;
    private StepResult result;
    private Instant startTime;
    private Duration duration;
    private String summary;
    private StepExecutionContextDTO context;
    private String user;

    public StepRunResultDTO(UUID runId, String alias, StepDefinitionDTO step, StepResult result, Instant startTime, Duration duration, String summary, StepExecutionContextDTO context, String user) {
        this.runId = runId;
        this.alias = alias;
        this.step = step;
        this.result = result;
        this.startTime = startTime;
        this.duration = duration;
        this.summary = summary;
        this.context = context;
        this.user = user;
    }

    public UUID runId() {
        return runId;
    }

    public String name() {
        return step.name();
    }

    public String alias() {
        return alias;
    }

    public StepResult result() {
        return result;
    }

    public Instant startTime() {
        return startTime;
    }

    public Duration duration() {
        return duration;
    }

    public String summary() {
        return summary;
    }

    public StepExecutionContextDTO context() {
        return context;
    }

    public StepDefinitionDTO step() {
        return step;
    }

    public String user() {
        return user;
    }

    public StepRunResultDTO fix() {
        context.fix();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepRunResultDTO that = (StepRunResultDTO) o;

        return Objects.equals(runId, that.runId);
    }

    @Override
    public int hashCode() {
        return runId != null ? runId.hashCode() : 0;
    }
}
