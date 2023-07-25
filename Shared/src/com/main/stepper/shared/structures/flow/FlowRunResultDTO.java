package com.main.stepper.shared.structures.flow;

import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.gson.FixerUtility;
import com.main.stepper.shared.structures.step.StepRunResultDTO;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class FlowRunResultDTO {
    private FlowInfoDTO flowInfo;
    private UUID runId;
    private FlowResult result;
    private Instant startTime;
    private Duration duration;
    private HashMap<FlowInfoDTO, HashMap<DataIODTO, DataIODTO>> customContinuationMappings;
    private HashMap<DataIODTO, Object> userInputs;
    private HashMap<DataIODTO, Object> internalOutputs;
    private HashMap<DataIODTO, Object> flowOutputs;
    private ArrayList<UUID> stepRunUUIDs;
    private ArrayList<StepRunResultDTO> stepRunResults;
    private FlowExecutionContextDTO context;
    private String user;

    public FlowRunResultDTO(FlowInfoDTO flowInfo, UUID runId, FlowResult result, Instant startTime, Duration duration, Map<FlowInfoDTO, HashMap<DataIODTO, DataIODTO>> customContinuationMappings, Map<DataIODTO, Object> userInputs, Map<DataIODTO, Object> internalOutputs, Map<DataIODTO, Object> flowOutputs, List<UUID> stepRunUUIDs, List<StepRunResultDTO> stepRunResults, FlowExecutionContextDTO context, String user) {
        this.flowInfo = flowInfo;
        this.runId = runId;
        this.result = result;
        this.startTime = startTime;
        this.duration = duration;
        this.customContinuationMappings = new HashMap<>(customContinuationMappings);
        this.userInputs = new HashMap<>(userInputs);
        this.internalOutputs = new HashMap<>(internalOutputs);
        this.flowOutputs = new HashMap<>(flowOutputs);
        this.stepRunUUIDs = new ArrayList<>(stepRunUUIDs);
        this.stepRunResults = new ArrayList<>(stepRunResults);
        this.context = context;
        this.user = user;
    }

    public UUID runId() {
        return runId;
    }

    public String name() {
        return flowInfo.name();
    }

    public FlowResult result() {
        return result;
    }

    public Instant startTime() {
        return startTime;
    }

    public Duration duration() {
        return duration;
    }
    public Map<DataIODTO, DataIODTO> continuationMapping(FlowInfoDTO flowInfoDTO) {
        return customContinuationMappings.get(flowInfoDTO);
    }

    public Map<DataIODTO, Object> userInputs() {
        return userInputs;
    }

    public Map<DataIODTO, Object> internalOutputs() {
        return internalOutputs;
    }

    public Map<DataIODTO, Object> flowOutputs() {
        return flowOutputs;
    }

    public List<UUID> stepRunUUID() {
        return stepRunUUIDs;
    }

    public List<StepRunResultDTO> stepRunResults() {
        return stepRunResults;
    }

    public FlowExecutionContextDTO flowExecutionContext() {
        return context;
    }

    public FlowInfoDTO flowInfo() {
        return flowInfo;
    }

    public String user() {
        return user;
    }

    public FlowRunResultDTO fix() {
        FixerUtility.fixMap(userInputs);
        FixerUtility.fixMap(internalOutputs);
        FixerUtility.fixMap(flowOutputs);
        context.fix();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowRunResultDTO that = (FlowRunResultDTO) o;

        if (!Objects.equals(runId, that.runId)) return false;
        if (result != that.result) return false;
        if (!Objects.equals(user, that.user)) return false;
        if (stepRunResults.size() != that.stepRunResults.size()) return false;
        return Objects.equals(stepRunResults, that.stepRunResults);
    }

    @Override
    public int hashCode() {
        int result1 = runId != null ? runId.hashCode() : 0;
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (stepRunResults != null ? stepRunResults.hashCode() : 0);
        return result1;
    }
}
