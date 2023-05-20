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

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.List;

public class SpendSomeTimeStep extends AbstractStepDefinition {

    public SpendSomeTimeStep() {
        super("Spend Some Time", true);
        addInput(new DataIO("TIME_TO_SPEND", "Total sleeping time (sec)", DataNecessity.MANDATORY, DDRegistry.NUMBER));
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Temporal startTime = LocalTime.now();

        List<IDataIO> inputs = getInputs();
        IDataIO timeToSpendIO = inputs.get(0);
        Integer timeToSpend = (Integer) context.getInput(timeToSpendIO, DDRegistry.NUMBER.getType());

        if(timeToSpend <= 0){
            context.log("Time to spend must be greater than 0!");

            Duration duration = Duration.between(startTime, LocalTime.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, duration, "Time to spend must be greater than 0!");
        }

        context.log("About to sleep for " + timeToSpend + " seconds...");
        try {
            Thread.sleep(timeToSpend * 1000);
        } catch (InterruptedException e) {
            context.log("Thread interrupted while sleeping.");
        }
        context.log("Done sleeping...");

        Duration duration = Duration.between(startTime, LocalTime.now());
        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, duration, "Success");
    }
}
