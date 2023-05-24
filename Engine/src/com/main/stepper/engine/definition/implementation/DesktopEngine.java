package com.main.stepper.engine.definition.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.api.IFlowExecutor;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import com.main.stepper.exceptions.engine.NotAFileException;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.flow.execution.implementation.FlowExecutionContext;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.logger.implementation.maplogger.MapLogger;
import com.main.stepper.statistics.StatManager;
import com.main.stepper.xml.generated.ex2.STFlow;
import com.main.stepper.xml.generated.ex2.STStepper;
import com.main.stepper.xml.parsing.api.IParser;
import com.main.stepper.xml.parsing.implementation.FlowParser;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateFlowOutputs;
import com.main.stepper.xml.validators.implementation.pipeline.Validator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DesktopEngine implements IEngine {
    private ILogger logger;
    private StatManager statistics;
    private List<IFlowDefinition> flows;
    private Boolean validated;
    private Executor executor;

    public DesktopEngine(){
        this.logger = new MapLogger();
        this.flows = Collections.synchronizedList(new ArrayList<>());
        this.validated = false;
        this.statistics = new StatManager();
        this.executor = null;
    }

    @Override
    public synchronized List<String> readSystemFromXML(String path) throws XMLException {
        // Read xml file
        IValidator pipelineValidator = new Validator(path);
        List<String> errors = pipelineValidator.validate();
        List<IFlowDefinition> fileFlows = new ArrayList<>();


        if(!errors.isEmpty())
            return errors;
        // TODO: add thread pool and continuations validators
        // Read system from validator
        STStepper stepper = (STStepper) pipelineValidator.getAdditional().get();
        List<STFlow> stFlows = stepper.getSTFlows().getSTFlow();
        for(STFlow stFlow : stFlows){
            // Validate no duplicate formal outputs
            IValidator validateNoDuplicateFlowOutputs = new ValidateNoDuplicateFlowOutputs(stFlow);
            errors.addAll(validateNoDuplicateFlowOutputs.validate());
            if(!errors.isEmpty())
                return errors;

            IParser flowParser = new FlowParser(stFlow);
            errors.addAll(flowParser.parse());
            if(!errors.isEmpty())
                return errors;
            IFlowDefinition flow = flowParser.get();
            errors.addAll(flow.validateFlowStructure());
            fileFlows.add(flow);
        }

        if(errors.isEmpty()){
            validated = true;
            // Reset all
            flows.clear();
            flows.addAll(fileFlows);
            logger.clear();
            statistics.clear();
            if(stepper.getSTThreadPool() == 0)
                executor = Executors.newFixedThreadPool(1);
            else
                executor = Executors.newFixedThreadPool(stepper.getSTThreadPool());
        }

        return errors;
    }

    @Override
    public List<String> getFlowNames() {
        return flows.stream().map(IFlowDefinition::name).collect(Collectors.toList());
    }

    @Override
    public List<IFlowDefinition> getFlows() {
        return flows;
    }

    @Override
    public IFlowInformation getFlowInfo(String name) {
        IFlowDefinition requested = flows.stream().filter(flow -> flow.name().equals(name)).findFirst().orElse(null);
        if(requested == null)
            return null;
        return requested.information();
    }

    @Override
    public ExecutionUserInputs getExecutionUserInputs(String flowName) {
        IFlowDefinition requested = flows.stream().filter(flow -> flow.name().equals(flowName)).findFirst().orElse(null);
        if(requested == null)
            return null;
        List<IDataIO> openInputs = new ArrayList<>();
        openInputs.addAll(requested.userRequiredInputs());
        openInputs.addAll(requested.userOptionalInputs());

        Map<IDataIO, IStepUsageDeclaration> mapping = new HashMap<>();
        openInputs.forEach(i->{
            mapping.put(i, requested.stepRequiringMandatoryInput(i));
        });

        return new ExecutionUserInputs(openInputs, mapping);
    }

    @Override
    public IFlowRunResult runFlow(String name, ExecutionUserInputs inputs) {
        Optional<IFlowDefinition> maybeFlow = flows.stream().filter(flow -> flow.name().equals(name)).findFirst();
        if(!maybeFlow.isPresent()){
            return null;
        }

        IFlowDefinition requested = maybeFlow.get();
        IFlowExecutionContext context;
        synchronized (requested){
            context = new FlowExecutionContext(
                    requested.mappings(),
                    logger,
                    statistics
            );

            for(IDataIO input : inputs.getUserInputs().keySet()){
                context.setVariable(input, inputs.getUserInputs().get(input));
            }

        }
        IFlowExecutor executor = new FlowExecutor();
        this.executor.execute(()->executor.executeFlow(requested, context));
        return null;
    }

    @Override
    public List<IFlowRunResult> getFlowRuns() {
        return null;
    }

    @Override
    public IFlowRunResult getFlowRunInfo(String runId) {
        return null;
    }

    @Override
    public StatManager getStatistics() {
        return statistics;
    }

    @Override
    public List<Log> getLogs(String uuid) {
        return logger.getLog(uuid);
    }

    @Override
    public void writeSystemToFile(String path) throws NotAFileException, IOException {

    }

    @Override
    public Boolean readSystemFromFile(String path) throws NotAFileException, IOException {
        return null;
    }
}
