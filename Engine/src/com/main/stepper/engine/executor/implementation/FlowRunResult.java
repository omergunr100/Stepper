package com.main.stepper.engine.executor.implementation;

import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class FlowRunResult implements IFlowRunResult {
    private final UUID runId;
    private final String name;
    private FlowResult result;
    private final Instant startTime;
    private Duration duration;
    private final Map<IDataIO, Object> userInputs;
    private final Map<IDataIO, Object> internalOutputs;
    private final Map<IDataIO, Object> flowOutputs;
    private final List<UUID> stepRunUUID;
    private final List<IStepRunResult> stepRunResults;
    private IFlowExecutionContext flowExecutionContext;
    private IFlowDefinition flowDefinition;
    private String user;

    public FlowRunResult(String user, UUID runId, String name, FlowResult result, Instant startTime, Duration duration, Map<IDataIO, Object> userInputs, Map<IDataIO, Object> internalOutputs, Map<IDataIO, Object> flowOutputs, List<UUID> stepRunUUID, List<IStepRunResult> stepRunResults, IFlowExecutionContext context, IFlowDefinition flowDefinition) {
        this.user = user;
        this.runId = runId;
        this.name = name;
        this.result = result;
        this.startTime = startTime;
        this.duration = duration;
        this.userInputs = userInputs;
        this.internalOutputs = internalOutputs;
        this.flowOutputs = flowOutputs;
        this.stepRunUUID = stepRunUUID;
        this.stepRunResults = stepRunResults;
        this.flowExecutionContext = context;
        this.flowDefinition = flowDefinition;
    }

    public FlowRunResult(String user, UUID runId, String name, Instant startTime, Map<IDataIO, Object> userInputs, IFlowExecutionContext context, IFlowDefinition flowDefinition) {
        this.user = user;
        this.runId = runId;
        this.name = name;
        this.result = FlowResult.RUNNING;
        this.startTime = startTime;
        this.userInputs = userInputs;
        this.flowExecutionContext = context;
        this.flowDefinition = flowDefinition;

        this.duration = null;
        this.internalOutputs = new HashMap<>();
        this.flowOutputs = new HashMap<>();
        this.stepRunUUID = new ArrayList<>();
        this.stepRunResults = new ArrayList<>();
    }

    @Override
    public UUID runId() {
        return runId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public FlowResult result() {
        return result;
    }

    @Override
    public Instant startTime() {
        return startTime;
    }

    @Override
    public Duration duration() {
        if (duration == null)
            return Duration.between(startTime, Instant.now());
        return duration;
    }

    @Override
    public Map<IDataIO, Object> userInputs() {
        return userInputs;
    }

    @Override
    public Map<IDataIO, Object> internalOutputs() {
        return internalOutputs;
    }

    @Override
    public Map<IDataIO, Object> flowOutputs() {
        return flowOutputs;
    }

    @Override
    public List<UUID> stepRunUUID() {
        return stepRunUUID;
    }

    @Override
    public List<IStepRunResult> stepRunResults() {
        return stepRunResults;
    }

    @Override
    public void setResult(FlowResult result) {
        this.result = result;
    }

    @Override
    public void addInternalOutput(IDataIO dataIO, Object value) {
        internalOutputs.put(dataIO, value);
    }

    @Override
    public void addFlowOutput(IDataIO dataIO, Object value) {
        flowOutputs.put(dataIO, value);
    }

    @Override
    public void addStepRunUUID(UUID stepRunUUID) {
        this.stepRunUUID.add(stepRunUUID);
    }

    @Override
    public void addStepRunResult(IStepRunResult stepRunResult) {
        stepRunResults.add(stepRunResult);
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public IFlowExecutionContext flowExecutionContext() {
        return flowExecutionContext;
    }

    @Override
    public IFlowDefinition flowDefinition() {
        return flowDefinition;
    }

    @Override
    public String user() {
        return user;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public FlowRunResultDTO toDTO() {
        Map<FlowInfoDTO, HashMap<DataIODTO, DataIODTO>> continuationMappings = flowDefinition.continuations().isEmpty() ? new HashMap<>() : flowDefinition.continuations().isEmpty() ? new HashMap<>() : flowDefinition.continuations().stream().collect(Collectors.toMap(
                cont -> cont.information().toDTO(),
                cont -> flowDefinition.continuationMapping(cont).entrySet().isEmpty() ? new HashMap<>() : new HashMap<>(flowDefinition.continuationMapping(cont).entrySet().stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getKey().toDTO(), v.getValue().toDTO()), HashMap::putAll)))
        );
        return new FlowRunResultDTO(
                flowDefinition.information().toDTO(),
                runId,
                result,
                startTime,
                duration(),
                continuationMappings,
                userInputs == null || userInputs.isEmpty() ? new HashMap<>() : userInputs.entrySet().stream().collect(HashMap::new, (m, v) -> m.put(v.getKey().toDTO(), v.getValue()), HashMap::putAll),
                internalOutputs == null || internalOutputs.isEmpty() ? new HashMap<>() : internalOutputs.entrySet().stream().collect(HashMap::new, (m, v) -> m.put(v.getKey().toDTO(), v.getValue()), HashMap::putAll),
                flowOutputs == null || flowOutputs.isEmpty() ? new HashMap<>() : flowOutputs.entrySet().stream().collect(HashMap::new, (m, v) -> m.put(v.getKey().toDTO(), v.getValue()), HashMap::putAll),
                stepRunUUID,
                stepRunResults == null || stepRunResults.isEmpty() ? new ArrayList<>() : stepRunResults.stream().map(IStepRunResult::toDTO).collect(Collectors.toList()),
                flowExecutionContext.toDTO(),
                user
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowRunResult that = (FlowRunResult) o;

        return Objects.equals(runId, that.runId);
    }

    @Override
    public int hashCode() {
        return runId != null ? runId.hashCode() : 0;
    }
}
