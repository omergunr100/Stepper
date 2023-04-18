package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

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
