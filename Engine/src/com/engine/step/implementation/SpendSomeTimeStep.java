package com.engine.step.implementation;

import com.engine.data.DDRegistry;
import com.engine.data.implementation.numeric.NumberDef;
import com.engine.io.api.DataNecessity;
import com.engine.io.api.IDataIO;
import com.engine.io.implementation.DataIO;
import com.engine.step.api.AbstractStepDefinition;
import com.engine.step.api.IStepExecutionContext;
import com.engine.step.api.StepResult;

public class SpendSomeTimeStep extends AbstractStepDefinition {

    public SpendSomeTimeStep() {
        super("Spend Some Time", true);
        addInput(new DataIO("TIME_TO_SPEND", "Total sleeping time (sec):", DataNecessity.MANDATORY, new NumberDef()));
    }

    @Override
    public StepResult execute(IStepExecutionContext context) {
        IDataIO timeToSpendIO = getInputs().get(0);
        Integer timeToSpend = context.getInput(timeToSpendIO.getName(), DDRegistry.NUMBER);

        if(timeToSpend <= 0){
            context.Log("\""+timeToSpendIO.getName()+"\" must be greater than 0!");
            context.setSummary("\""+timeToSpendIO.getUserString()+"\" must be greater than 0!");
            return StepResult.FAILURE;
        }

        context.Log("About to sleep for " + timeToSpend + " seconds...");
        try {
            Thread.sleep(timeToSpend * 1000);
        } catch (InterruptedException e) {
            context.Log("Thread interrupted while sleeping.");
        }
        context.Log("Done sleeping...");

        return StepResult.SUCCESS;
    }
}
