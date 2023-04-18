package com.main.stepper.xml.parsing.implementation;

import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.flow.definition.api.StepUsageDeclaration;
import com.main.stepper.step.definition.StepRegistry;
import com.main.stepper.xml.generated.STStepInFlow;
import com.main.stepper.xml.parsing.api.IParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StepParser implements IParser {
    private STStepInFlow step;
    private IStepUsageDeclaration stepUsageDeclaration = null;

    public StepParser(STStepInFlow step) {
        this.step = step;
    }

    @Override
    public List<String> parse() {
        List<String> errors = new ArrayList<>();

        // Find first step in registry matching the name of the step
        Optional<StepRegistry> stepDefinition = Arrays.stream(StepRegistry.values())
                .filter(s -> s.getName().equals(step.getName()))
                .findFirst();
        if(!stepDefinition.isPresent()){
            errors.add("Step " + step.getName() + " is not defined in the system.");
            return errors;
        }

        // Find out if the step is skip if failed or not
        Optional<Boolean> skipIfFailed = Optional.ofNullable(step.isContinueIfFailing());
        if(!skipIfFailed.isPresent())
            skipIfFailed = Optional.of(false);

        // Find out if the step has an alias
        Optional<String> alias = Optional.ofNullable(step.getAlias());
        if(!alias.isPresent())
            alias = Optional.of(step.getName());

        // Create the step usage declaration
        stepUsageDeclaration = new StepUsageDeclaration(stepDefinition.get(), alias.get(), skipIfFailed.get());

        return errors;
    }

    @Override
    public Optional<IStepUsageDeclaration> get() {
        return Optional.ofNullable(stepUsageDeclaration);
    }
}
