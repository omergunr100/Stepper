package com.main.stepper.engine.executor.api;

import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.io.api.IDataIO;

import java.time.Duration;
import java.util.Map;

public interface IFlowRunResult {
    String runId();

    String name();

    FlowResult result();

    Duration duration();

    Map<IDataIO, Object> userInputs();

    Map<IDataIO, Object> flowOutputs();
}
