package com.main.stepper.xml.parsing;

import com.main.stepper.xml.generated.STFlow;
import com.main.stepper.xml.generated.STStepInFlow;
import com.main.stepper.xml.generated.STStepper;

public final class Parser {
    private STStepper stepper;
    private STFlow flow;
    private STStepInFlow step;

    public Parser(STStepper stepper) {
        this.stepper = stepper;
    }

    public Parser(STFlow flow) {
        this.flow = flow;
    }

    public Parser(STStepInFlow step) {
        this.step = step;
    }



}
