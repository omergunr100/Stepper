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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ConsoleEngine implements IEngine {
    private ILogger logger;
    private StatManager statistics;
    private List<IFlowDefinition> flows;
    private Boolean validated;

    public ConsoleEngine(){
        this.logger = new MapLogger();
        this.flows = new ArrayList<>();
        this.validated = false;
        this.statistics = new StatManager();
    }

    private Optional<IFlowDefinition> getFlowByName(String name){
        return flows.stream().filter(f->f.name().equals(name)).findFirst();
    }

    @Override
    public List<String> readSystemFromXMLString(String xmlFileContent) throws XMLException {
        return null;
    }

    @Override
    public List<String> readSystemFromXML(String path) throws XMLException {
        // Read xml file
        IValidator pipelineValidator = new Validator(path);
        List<String> errors = pipelineValidator.validate();
        List<IFlowDefinition> fileFlows = new ArrayList<>();

        if(!errors.isEmpty())
            return errors;

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
        }

        return errors;
    }

    @Override
    public List<String> getFlowNames() {
        return flows.stream()
                .map(IFlowDefinition::name)
                .collect(Collectors.toList());
    }

    @Override
    public List<IFlowDefinition> getFlows() {
        return flows;
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
    public List<IFlowRunResult> getFlowRuns() {
        return statistics.getFlowRuns();
    }

    @Override
    public IFlowRunResult getFlowRunInfo(String runId) {
        return statistics.getFlowRuns()
                .stream()
                .filter(result->result.runId().equals(runId))
                .findFirst()
                .orElse(null);
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
        File file = new File(path);
        if(!file.exists()) {
            if(file.getParentFile() == null)
                throw new NotAFileException();

            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        if(!file.isFile())
            throw new NotAFileException();


        try(
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fout);
                ){
            out.writeObject(logger);
            out.writeObject(statistics);
            out.writeObject(flows);
            out.writeObject(validated);
        } catch (IOException e){
            throw e;
        }
    }

    @Override
    public Boolean readSystemFromFile(String path) throws NotAFileException, IOException {
        File file = new File(path);
        if(!file.exists())
            return false;
        if(!file.isFile())
            throw new NotAFileException();

        try (
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fin)
                ){
            ILogger tLogger = (ILogger) in.readObject();
            StatManager tStatistics = (StatManager) in.readObject();
            List<IFlowDefinition> tFlows = (List<IFlowDefinition>) in.readObject();
            Boolean tValidated = (Boolean) in.readObject();

            logger = tLogger;
            statistics = tStatistics;
            flows = tFlows;
            validated = tValidated;
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }
}
