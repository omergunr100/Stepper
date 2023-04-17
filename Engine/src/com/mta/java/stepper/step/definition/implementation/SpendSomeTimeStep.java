package com.mta.java.stepper.step.definition.implementation;

import com.mta.java.stepper.data.DDRegistry;
import com.mta.java.stepper.io.api.DataNecessity;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.io.implementation.DataIO;
import com.mta.java.stepper.step.definition.api.AbstractStepDefinition;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;
import com.mta.java.stepper.step.definition.api.StepResult;

public class SpendSomeTimeStep extends AbstractStepDefinition {

    public SpendSomeTimeStep() {
        super("Spend Some Time", true);
        addInput(new DataIO("TIME_TO_SPEND", "Total sleeping time (sec)", DataNecessity.MANDATORY, DDRegistry.NUMBER));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        IDataIO timeToSpendIO = getInputs().get(0);
        Integer timeToSpend = (Integer) context.getInput(timeToSpendIO, DDRegistry.NUMBER.getType());

        if(timeToSpend <= 0){
            context.log("\""+timeToSpendIO.getName()+"\" must be greater than 0!");
            context.setSummary("\""+timeToSpendIO.getUserString()+"\" must be greater than 0!");
            return StepResult.FAILURE;
        }

        context.log("About to sleep for " + timeToSpend + " seconds...");
        try {
            Thread.sleep(timeToSpend * 1000);
        } catch (InterruptedException e) {
            context.log("Thread interrupted while sleeping.");
        }
        context.log("Done sleeping...");

        return StepResult.SUCCESS;
    }
}
