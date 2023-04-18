package com.main.stepper.engine.executor.api;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;

public interface IFlowExecutor {
     IFlowRunResult executeFlow(IFlowDefinition flow, IFlowExecutionContext context);
     static IFlowExecutor getInstance(){return null;}
}
