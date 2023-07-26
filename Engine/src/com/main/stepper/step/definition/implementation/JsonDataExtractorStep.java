package com.main.stepper.step.definition.implementation;

import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.main.stepper.data.DDRegistry;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class JsonDataExtractorStep extends AbstractStepDefinition {

    public JsonDataExtractorStep() {
        super("Json Data Extractor", true);
        addInput(new DataIO("JSON", "Json source", DataNecessity.MANDATORY, DDRegistry.JSON));
        addInput(new DataIO("JSON_PATH", "Data", DataNecessity.MANDATORY, DDRegistry.STRING));
        addOutput(new DataIO("VALUE", "Data value", DDRegistry.STRING));
    }

    @Override
    public Class<? extends AbstractStepDefinition> getStepClass() {
        return JsonDataExtractorStep.class;
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Instant startTime = Instant.now();

        // Read inputs and output
        JsonObject json = (JsonObject) context.getInput(getInputs().get(0), DDRegistry.JSON.getType());
        String jsonPath = (String) context.getInput(getInputs().get(1), DDRegistry.STRING.getType());

        IDataIO value = getOutputs().get(0);

        // Check if json is available
        if (json == null || jsonPath == null || jsonPath.isEmpty()) {
            context.log("Didn't receive any json or json path");
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, startTime, Duration.between(startTime, Instant.now()), "Didn't receive any json or json path");
        }

        // Split json path to list of paths and parse json document
        List<String> jsonPaths = Arrays.asList(jsonPath.split("\\|"));
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        Object document = conf.jsonProvider().parse(json.toString());
        StringBuilder result = new StringBuilder();
        for (String path : jsonPaths) {
            String current = JsonPath.read(document, path);
            if (current != null) {
                context.log("Extracting data " + path + ". Value: " + current);
                result.append(current);
                result.append(",");
            }
            else {
                context.log("No value found for json path " + path);
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        // Write output
        context.setOutput(value, result.toString());
        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, startTime, Duration.between(startTime, Instant.now()), "Successfully extracted data");
    }
}
