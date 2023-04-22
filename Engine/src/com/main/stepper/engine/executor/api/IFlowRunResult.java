package com.main.stepper.engine.executor.api;

import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.io.api.IDataIO;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Map;

public interface IFlowRunResult {
    String runId();

    String name();

    FlowResult result();

    Temporal startTime();

    Duration duration();

    Map<IDataIO, Object> userInputs();

    Map<IDataIO, Object> internalOutputs();

    Map<IDataIO, Object> flowOutputs();
}
