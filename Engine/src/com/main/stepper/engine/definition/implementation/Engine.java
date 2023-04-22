package com.main.stepper.engine.definition.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.api.IFlowExecutor;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.flow.execution.implementation.FlowExecutionContext;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.maplogger.MapLogger;
import com.main.stepper.statistics.StatManager;
import com.main.stepper.xml.generated.STFlow;
import com.main.stepper.xml.generated.STStepper;
import com.main.stepper.xml.parsing.api.IParser;
import com.main.stepper.xml.parsing.implementation.FlowParser;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.pipeline.Validator;

import java.util.*;
import java.util.stream.Collectors;

public final class Engine implements IEngine {
    private final ILogger logger;
    private final StatManager statistics;
    private List<IFlowDefinition> flows;
    private Boolean validated;

    public Engine(){
        this.logger = new MapLogger();
        this.flows = new ArrayList<>();
        this.validated = false;
        this.statistics = new StatManager();
    }

    private Optional<IFlowDefinition> getFlowByName(String name){
        return flows.stream().filter(f->f.name().equals(name)).findFirst();
    }

    @Override
    public List<String> readSystemFromXML(String path) throws XMLException {
        IValidator pipelineValidator = new Validator(path);
        List<String> errors = pipelineValidator.validate();
        if(errors.isEmpty()){
            // Read system from validator
            STStepper stepper = (STStepper) pipelineValidator.getAdditional().get();
            List<STFlow> stFlows = stepper.getSTFlows().getSTFlow();
            for(STFlow stFlow : stFlows){
                IParser flowParser = new FlowParser(stFlow);
                IFlowDefinition flow = flowParser.parse();
                errors.addAll(flow.validateFlowStructure());
                if(!errors.isEmpty()){
                    flows.clear();
                    return errors;
                }
                flows.add(flow);
            }
        }

        if(errors.isEmpty())
            validated = true;

        return errors;
    }

    @Override
    public List<String> getFlowNames() {
        return flows.stream()
                .map(IFlowDefinition::name)
                .collect(Collectors.toList());
    }

    @Override
    public IFlowInformation getFlowInfo(String name) {
        Optional<IFlowDefinition> flow = getFlowByName(name);
        if(!flow.isPresent())
            return null;
        return flow.get().information();
    }

    @Override
    public ExecutionUserInputs getExecutionUserInputs(String flowName) {
        IFlowDefinition flow = getFlowByName(flowName).get();
        List<IDataIO> openInputs = new ArrayList<>();
        openInputs.addAll(flow.userRequiredInputs());
        openInputs.addAll(flow.userOptionalInputs());

        Map<IDataIO, IStepUsageDeclaration> mapping = new HashMap<>();
        openInputs.forEach(i->{
            mapping.put(i, flow.stepRequiringMandatoryInput(i));
        });

        return new ExecutionUserInputs(openInputs, mapping);
    }

    @Override
    public IFlowRunResult runFlow(String name, ExecutionUserInputs inputs) {
        Optional<IFlowDefinition> maybeFlow = flows.stream().filter(f->f.name().equals(name)).findFirst();
        if(!maybeFlow.isPresent())
            return null;

        IFlowDefinition flow = maybeFlow.get();
        IFlowExecutionContext context = new FlowExecutionContext(
                flow.mappings(),
                logger,
                statistics
        );

        for(IDataIO input : inputs.getUserInputs().keySet()){
            context.setVariable(input, inputs.getUserInputs().get(input));
        }

        IFlowExecutor executor = new FlowExecutor();
        IFlowRunResult result = executor.executeFlow(flow, context);
        return result;
    }

    @Override
    public List<String> getFlowRuns() {
        return null;
    }

    @Override
    public IFlowRunResult getFlowRunInfo(UUID runId) {
        return null;
    }

    @Override
    public String getStatistics() {
        return null;
    }

    @Override
    public List<IDataIO> getFreeMandatoryInputs(String flowName) {
        return null;
    }

    @Override
    public List<IDataIO> getFreeOptionalInputs(String flowName) {
        return null;
    }
}
