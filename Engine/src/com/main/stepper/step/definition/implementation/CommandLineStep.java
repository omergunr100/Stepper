package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.List;

public class CommandLineStep extends AbstractStepDefinition {
    public CommandLineStep() {
        super("Command Line", false);
        addInput(new DataIO("COMMAND", "Command", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("ARGUMENTS", "Command arguments", DataNecessity.OPTIONAL, DDRegistry.STRING));
        addOutput(new DataIO("RESULT", "Command output", DDRegistry.STRING));
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Temporal startTime = LocalTime.now();

        // Read inputs and output
        List<IDataIO> inputs = getInputs();
        String command = (String) context.getInput(inputs.get(0), DDRegistry.STRING.getType());
        String arguments = (String) context.getInput(inputs.get(1), DDRegistry.STRING.getType());

        List<IDataIO> outputs = getOutputs();
        IDataIO result = outputs.get(0);

        // Log operation
        context.log("About to invoke " + command + " " + arguments);

        // Make command using process builder
        ProcessBuilder pb = new ProcessBuilder(command, arguments);
        // Run command
        String resultString = "";
        try {
            Process p = pb.start();
            // Read process builder output to string
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            resultString = builder.toString();
        } catch (IOException ignored) {
        }

        context.setOutput(result, resultString);

        Duration duration = Duration.between(startTime, LocalTime.now());
        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "Success");
    }
}
