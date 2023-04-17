package com.mta.java.stepper.main;

import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.logger.api.ILogger;
import com.mta.java.stepper.logger.implementation.MapLogger;
import com.mta.java.stepper.logger.implementation.data.Log;
import com.mta.java.stepper.step.definition.StepRegistry;
import com.mta.java.stepper.step.definition.api.IStepDefinition;
import com.mta.java.stepper.step.definition.api.StepResult;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;
import com.mta.java.stepper.step.execution.implementation.StepExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
         // Test for SpendSomeTime

//        IStepDefinition step = StepRegistry.SPEND_SOME_TIME;
//        Map<IDataIO, Object> variables = new HashMap<>();
//        IDataIO time = step.getInputs().get(0);
//        variables.put(time, 3);
//        Map<IDataIO, IDataIO> mapping = new HashMap<>();
//        mapping.put(time, time);
//        ILogger logger = new MapLogger();
//        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));
//        StepResult result = step.execute(context);
//        System.out.println("Result: " + result);
//        System.out.println("The logs:");
//        for(Log log : logger.getLog("Step")){
//            System.out.println(log);
//        }

        // Test for Collect Files In Folder

//        IStepDefinition step = StepRegistry.COLLECT_FILES_IN_FOLDER;
//        Map<IDataIO, Object> variables = new HashMap<>();
//        IDataIO folder = step.getInputs().get(0);
//        IDataIO extension = step.getInputs().get(1);
//        IDataIO files = step.getOutputs().get(0);
//        IDataIO count = step.getOutputs().get(1);
//        variables.put(folder, "C:\\Users\\omere\\Desktop\\Test");
//        variables.put(extension, ".txt");
//        Map<IDataIO, IDataIO> mapping = new HashMap<>();
//        mapping.put(folder, folder);
//        mapping.put(extension, extension);
//        mapping.put(files, files);
//        mapping.put(count, count);
//        ILogger logger = new MapLogger();
//        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));
//        StepResult result = step.execute(context);
//        System.out.println("Result: " + result);
//        System.out.println("The logs:");
//        for(Log log : logger.getLog("Step")){
//            System.out.println(log);
//        }

        //
    }
}
