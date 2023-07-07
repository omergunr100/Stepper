package com.main.stepper.shared.structures.step;

import java.util.Objects;

public class StepUsageDTO {
    private String name;
    private StepDefinitionDTO step;
    private Boolean skipIfFailed;

    public StepUsageDTO(String name, StepDefinitionDTO step, Boolean skipIfFailed) {
        this.name = name;
        this.step = step;
        this.skipIfFailed = skipIfFailed;
    }


    public String name() {
        return name;
    }

    public StepDefinitionDTO step() {
        return step;
    }

    public Boolean skipIfFailed() {
        return skipIfFailed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepUsageDTO that = (StepUsageDTO) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(step, that.step)) return false;
        return Objects.equals(skipIfFailed, that.skipIfFailed);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (step != null ? step.hashCode() : 0);
        result = 31 * result + (skipIfFailed != null ? skipIfFailed.hashCode() : 0);
        return result;
    }
}
