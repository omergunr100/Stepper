package com.main.stepper.step.definition.implementation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.main.stepper.data.DDRegistry;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.time.Duration;
import java.time.Instant;

public class JasonatorStep extends AbstractStepDefinition {

    public JasonatorStep() {
        super("To Json", true);
        addInput(new DataIO("CONTENT", "Content", DataNecessity.MANDATORY, DDRegistry.STRING));
        addOutput(new DataIO("JSON", "Json representation", DDRegistry.JSON));
    }

    @Override
    public Class<? extends AbstractStepDefinition> getStepClass() {
        return JasonatorStep.class;
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Instant startTime = Instant.now();

        // Read input and output
        String content = (String) context.getInput(getInputs().get(0), DDRegistry.STRING.getType());
        DataIO json = (DataIO) getOutputs().get(0);

        // Check if content is available
        if (content == null) {
            context.log("Didn't receive any content");
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, startTime, Duration.between(startTime, Instant.now()), "Didn't receive any content");
        }

        // Check if content is valid json object
        try {
            JsonObject object = JsonParser.parseString(content).getAsJsonObject();
            context.log("Content is a JSON string. Converting to JSON object...");
            context.setOutput(json, object);
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, startTime, Duration.between(startTime, Instant.now()), "Content is a JSON string. Successfully converted.");
        } catch (JsonParseException e) {
            context.log("Content is not a valid JSON representation");
            context.setOutput(json, null);
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, startTime, Duration.between(startTime, Instant.now()), "Content is not a valid JSON representation.");
        }
    }
}
