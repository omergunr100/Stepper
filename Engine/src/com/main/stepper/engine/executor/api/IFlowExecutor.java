package com.main.stepper.engine.executor.api;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;

import java.util.List;

public interface IFlowExecutor {
     IFlowRunResult executeFlow(IFlowDefinition flow, IFlowExecutionContext context);
}
