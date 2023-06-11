package com.main.stepper.engine.executor.api;

import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface IFlowRunResult extends Serializable {
    String runId();

    String name();

    FlowResult result();

    Instant startTime();

    Duration duration();

    Map<IDataIO, Object> userInputs();

    Map<IDataIO, Object> internalOutputs();

    Map<IDataIO, Object> flowOutputs();

    List<String> stepRunUUID();

    List<IStepRunResult> stepRunResults();

    void setResult(FlowResult result);

    void addInternalOutput(IDataIO dataIO, Object value);

    void addFlowOutput(IDataIO dataIO, Object value);

    void addStepRunUUID(String stepRunUUID);

    void addStepRunResult(IStepRunResult stepRunResult);

    void setDuration(Duration duration);

    IFlowExecutionContext flowExecutionContext();

    IFlowDefinition flowDefinition();
}
