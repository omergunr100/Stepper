package com.main.stepper.engine.definition.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.api.IFlowExecutor;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.engine.executor.implementation.FlowExecutor;
import com.main.stepper.exceptions.engine.NotAFileException;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.execution.api.IFlowExecutionContext;
import com.main.stepper.flow.execution.implementation.ServerFlowExecutionContext;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.logger.implementation.maplogger.MapLogger;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import com.main.stepper.shared.structures.executionuserinputs.ExecutionUserInputsDTO;
import com.main.stepper.xml.generated.ex2.STFlow;
import com.main.stepper.xml.generated.ex2.STStepper;
import com.main.stepper.xml.parsing.api.IParser;
import com.main.stepper.xml.parsing.implementation.FlowParser;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.flow.ValidateContinuationTypes;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateFlowOutputs;
import com.main.stepper.xml.validators.implementation.pipeline.Validator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ServerEngine implements IEngine {
    private final ILogger logger;
    private final List<IFlowDefinition> flows;
    private final List<IFlowRunResult> flowRunResults;
    private final List<IStepRunResult> stepRunResults;
    private Boolean validated;
    private ExecutorService executor;

    public ServerEngine() {
        logger = new MapLogger();
        flows = new ArrayList<>();
        flowRunResults = new ArrayList<>();
        stepRunResults = new ArrayList<>();
        validated = false;
        executor = null;
    }

    @Override
    public synchronized List<String> readSystemFromXMLString(String xmlFileContent) throws XMLException {
        // Read xml file
        IValidator pipelineValidator = new Validator(xmlFileContent, Validator.Type.STRING);
        List<String> errors = pipelineValidator.validate();
        List<IFlowDefinition> fileFlows = new ArrayList<>();

        if(!errors.isEmpty())
            return errors;

        // Read system from validator
        STStepper stepper = (STStepper) pipelineValidator.getAdditional().get();
        // Check if thread pool size is valid (if this is the first file)
        if (!validated) {
            if (stepper.getSTThreadPool() <= 0) {
                errors.add("Thread pool size must be a positive integer");
                return errors;
            }
        }
        // Validate flows
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

        // Insert continuations
        for (IFlowDefinition flow : fileFlows) {
            List<String> contNames = flow.continuationNames();
            for (String contName : contNames) {
                IFlowDefinition continuation = fileFlows.stream().filter(f -> f.name().equals(contName)).findFirst().orElse(null);
                if(continuation == null){
                    errors.add("Continuation: " + contName + " not found for flow: " + flow.name());
                    return errors;
                }
                else
                    flow.addContinuation(continuation);
            }
        }

        for (IFlowDefinition flow : fileFlows){
            IValidator validateContinuations = new ValidateContinuationTypes(flow);
            errors.addAll(validateContinuations.validate());
        }

        if(errors.isEmpty()){
            synchronized (flows) {
                // add all flows that aren't already in the system
                List<IFlowDefinition> newFlows = fileFlows.stream().filter(f -> flows.stream().noneMatch(f2 -> f2.name().equals(f.name()))).collect(Collectors.toList());
                flows.addAll(newFlows);
            }
            if (!validated) {
                if (stepper.getSTThreadPool() == 0)
                    executor = Executors.newFixedThreadPool(1);
                else
                    executor = Executors.newFixedThreadPool(stepper.getSTThreadPool());
            }
            validated = true;
        }

        return errors;
    }

    @Override
    public List<String> getFlowNames() {
        synchronized (flows) {
            return flows.stream().map(IFlowDefinition::name).collect(Collectors.toList());
        }
    }

    @Override
    public List<IFlowDefinition> getFlows() {
        synchronized (flows) {
            return new ArrayList<>(flows);
        }
    }

    @Override
    public IFlowInformation getFlowInfo(String name) {
        synchronized (flows) {
            Optional<IFlowDefinition> match = flows.stream().filter(f -> f.name().equals(name)).findFirst();
            if (match.isPresent())
                return match.get().information();
            else
                return null;
        }
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
    public UUID runFlow(String userCookie, String flowName, ExecutionUserInputsDTO inputs) {
        Optional<IFlowDefinition> maybeFlow;
        synchronized (flows) {
            maybeFlow = flows.stream().filter(flow -> flow.name().equals(flowName)).findFirst();
        }
        if(!maybeFlow.isPresent()){
            return null;
        }

        IFlowDefinition requested = maybeFlow.get();
        IFlowExecutionContext context;
        synchronized (requested){
            context = new ServerFlowExecutionContext(
                    userCookie,
                    requested.mappings(),
                    logger,
                    flowRunResults,
                    stepRunResults
            );

            for(DataIODTO input : inputs.getUserInputs().keySet()){
                context.setVariable(input.toDataIO(), inputs.getUserInputs().get(input));
            }

            for (Map.Entry<IDataIO, Object> entry : requested.initialValues().entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }

        }
        IFlowExecutor executor = new FlowExecutor();
        this.executor.execute(()->executor.executeFlow(requested, context));
        return context.getUniqueRunId();
    }

    @Override
    public List<IFlowRunResult> getFlowRuns() {
        synchronized (flowRunResults){
            return new ArrayList<>(flowRunResults);
        }
    }

    @Override
    public IFlowRunResult getFlowRunInfo(String runId) {
        synchronized (flowRunResults) {
            Optional<IFlowRunResult> match = flowRunResults.stream().filter(r -> r.runId().equals(runId)).findFirst();
            if (match.isPresent())
                return match.get();
            else
                return null;
        }
    }

    @Override
    public List<Log> getLogs(String uuid) {
        synchronized (logger) {
            return logger.getLog(uuid);
        }
    }

    @Override
    public List<IFlowRunResult> getFlowRunsFromList(List<UUID> uuids) {
        synchronized (flowRunResults) {
            return flowRunResults.stream().filter(r -> uuids.contains(r.runId())).collect(Collectors.toList());
        }
    }

    @Override
    public List<IStepRunResult> getStepRunsFromList(List<UUID> uuids) {
        synchronized (stepRunResults) {
            return stepRunResults.stream().filter(r -> uuids.contains(r.runId())).collect(Collectors.toList());
        }
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Irrelevant for server engine
     */
    @Override
    public void writeSystemToFile(String path) throws NotAFileException, IOException {

    }

    /**
     * Irrelevant for server engine
     */
    @Override
    public Boolean readSystemFromFile(String path) throws NotAFileException, IOException {
        return null;
    }

    /**
     * Irrelevant for server engine
     */
    @Override
    public List<String> readSystemFromXML(String path) throws XMLException {
        return null;
    }
}
