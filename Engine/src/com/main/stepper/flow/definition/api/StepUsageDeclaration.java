package com.main.stepper.flow.definition.api;

import com.main.stepper.step.definition.api.IStepDefinition;

public class StepUsageDeclaration implements IStepUsageDeclaration, Comparable<String>{
    private final String name;
    private final IStepDefinition step;
    private final Boolean skipIfFailed;

    public StepUsageDeclaration(IStepDefinition step, String name, Boolean skipIfFailed) {
        this.name = name;
        this.step = step;
        this.skipIfFailed = skipIfFailed;
    }

    public StepUsageDeclaration(IStepDefinition step, String name) {
        this(step, name, false);
    }


    public StepUsageDeclaration(IStepDefinition step) {
        this(step, step.getName(),false);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public IStepDefinition step() {
        return step;
    }

    @Override
    public Boolean skipIfFailed() {
        return skipIfFailed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (!o.getClass().equals(String.class) && getClass() != o.getClass())) return false;

        StepUsageDeclaration that = (StepUsageDeclaration) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "StepUsageDeclaration{" +
                "name='" + name + '\'' +
                ", skipIfFailed=" + skipIfFailed +
                ", step=" + step +
                '}';
    }

    @Override
    public int compareTo(String o) {
        return name.compareTo(o);
    }
}
