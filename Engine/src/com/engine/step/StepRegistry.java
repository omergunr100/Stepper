package com.engine.step;

import com.engine.step.api.IStepDefinition;
import com.engine.step.implementation.SpendSomeTimeStep;

public enum StepRegistry {
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    ;

    private final IStepDefinition step;

    StepRegistry(IStepDefinition step) {
        this.step = step;
    }

    public IStepDefinition getStep() {
        return step;
    }
}
