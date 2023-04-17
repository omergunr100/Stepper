package com.mta.java.stepper.engine.executor.api;

import com.mta.java.stepper.flow.definition.api.FlowResult;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.logger.implementation.data.Log;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface IFlowRunResult {
    String runId();

    String name();

    FlowResult result();

    Duration duration();

    Map<IDataIO, Object> userInputs();

    Map<IDataIO, Object> flowOutputs();
}
