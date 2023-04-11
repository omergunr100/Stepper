package com.engine.xml;

import com.engine.flows.Flow;
import com.engine.xml.data.mapping.STCustomMapping;
import com.engine.xml.flow.STFlow;
import com.engine.xml.steps.STStepInFlow;
import com.engine.xml.validation.STEPS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Parser {
    public static Flow parseStep(STFlow flow){
        Flow result = new Flow(flow.getName(), flow.getSTFlowDescription());

        List<STCustomMapping> custom = flow.getSTCustomMappings().getSTCustomMapping();
        flow.getSTFlowLevelAliasing();
        for(STStepInFlow step : flow.getSTStepsInFlow().getSTStepInFlow()){
            // match step name to one of the classes of STEPS
            STEPS stepEnum = Arrays.stream(STEPS.values()).filter((STEPS s) -> s.getName().equals(step.getName())).collect(Collectors.toList()).get(0);

        }


        return result;
    }
}
