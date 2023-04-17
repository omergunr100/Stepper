package com.mta.java.stepper.engine.executor.api;

import com.mta.java.stepper.flow.definition.api.IFlowDefinition;
import com.mta.java.stepper.flow.execution.api.IFlowExecutionContext;

public interface IFlowExecutor {
     IFlowRunResult executeFlow(IFlowDefinition flow, IFlowExecutionContext context);
     static IFlowExecutor getInstance(){return null;}
}
