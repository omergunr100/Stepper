package com.main.stepper.engine.definition.implementation;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.exceptions.engine.NotAFileException;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.logger.implementation.maplogger.MapLogger;
import com.main.stepper.statistics.StatManager;
import com.main.stepper.xml.generated.ex1.STFlow;
import com.main.stepper.xml.generated.ex1.STStepper;
import com.main.stepper.xml.parsing.api.IParser;
import com.main.stepper.xml.parsing.implementation.FlowParser;
import com.main.stepper.xml.validators.api.IValidator;
import com.main.stepper.xml.validators.implementation.flow.ValidateNoDuplicateFlowOutputs;
import com.main.stepper.xml.validators.implementation.pipeline.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesktopEngine implements IEngine {
    private ILogger logger;
    private StatManager statistics;
    private List<IFlowDefinition> flows;
    private Boolean validated;

    public DesktopEngine(){
        this.logger = new MapLogger();
        this.flows = new ArrayList<>();
        this.validated = false;
        this.statistics = new StatManager();
    }

    @Override
    public synchronized List<String> readSystemFromXML(String path) throws XMLException {
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
        return null;
    }

    @Override
    public List<IFlowDefinition> getFlows() {
        return null;
    }

    @Override
    public IFlowInformation getFlowInfo(String name) {
        return null;
    }

    @Override
    public ExecutionUserInputs getExecutionUserInputs(String flowName) {
        return null;
    }

    @Override
    public IFlowRunResult runFlow(String name, ExecutionUserInputs inputs) {
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
        return null;
    }

    @Override
    public List<Log> getLogs(String uuid) {
        return null;
    }

    @Override
    public void writeSystemToFile(String path) throws NotAFileException, IOException {

    }

    @Override
    public Boolean readSystemFromFile(String path) throws NotAFileException, IOException {
        return null;
    }
}
