package com.main.stepper.application.implementation.console;

import com.main.stepper.application.api.IApplication;
import com.main.stepper.application.implementation.console.data.parser.DataParser;
import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.definition.implementation.Engine;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
import com.main.stepper.exceptions.data.BadTypeException;
import com.main.stepper.exceptions.engine.NotAFileException;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.logger.implementation.data.Log;
import com.main.stepper.statistics.StatManager;
import com.main.stepper.step.definition.StepRegistry;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleApplication implements IApplication {
    public static void main(String[] args) {
        ConsoleApplication application = new ConsoleApplication(new Engine());
        while(application.run)
            application.presentMenu();
    }

    private boolean run = true;
    private final IEngine engine;
    private final Scanner scanner;

    public ConsoleApplication(IEngine engine) {
        this.engine = engine;
        this.scanner = new Scanner(System.in);
    }

    private Integer getChoiceOfFlow(){
        List<String> flowNames = engine.getFlowNames();
        if(flowNames.size() == 0){
            System.out.println("There are no flows in the system.");
            return -1;
        }
        System.out.println("Please choose one of the following flows: (or enter '0' to exit)");

        for(int i = 0; i < flowNames.size(); i++)
            System.out.println((i + 1) + ") " + flowNames.get(i));

        String input;
        Integer choice;
        do {
            input = scanner.nextLine();
            try{
                choice = Integer.parseInt(input);
                if(choice < 0 || choice > flowNames.size())
                    throw new NumberFormatException();
            }catch (NumberFormatException e){
                System.out.println("Invalid input, please try again.");
                choice = null;
            }
        }while(choice == null);

        return choice;
    }

    @Override
    public void presentMenu() {
        System.out.println("Main menu, please choose one of the following options:");
        System.out.println("1) Read system from file.");
        System.out.println("2) Show flow information.");
        System.out.println("3) Execute flow.");
        System.out.println("4) Show full information about a past run.");
        System.out.println("5) Get system statistics.");
        System.out.println("6) Create a backup file.");
        System.out.println("7) Load from backup file.");
        System.out.println("8) Exit.");

        String input = scanner.nextLine();
        switch (input){
            case "1":
                readSystemFromFile();
                break;
            case "2":
                showFlowInformation();
                break;
            case "3":
                executeFlow();
                break;
            case "4":
                pastRunFullInformation();
                break;
            case "5":
                getSystemStatistics();
                break;
            case "6":
                createSystemBackup();
                break;
            case "7":
                reloadFromBackup();
                break;
            case "8":
                exit();
                break;
            default:
                System.out.println("Invalid input, please try again.");
                break;
        }
        System.out.println();
    }

    @Override
    public void readSystemFromFile() {
        System.out.println("Please enter the path to the file:");
        String path = scanner.nextLine();
        List<String> errors;
        try{
            errors = engine.readSystemFromXML(path);
        } catch (XMLException e) {
            System.out.println("An error occurred while reading the file:");
            System.out.println(e.getMessage());
            return;
        }
        if(errors.size() > 0){
            System.out.println("The following errors were encountered:");
            for(String error : errors)
                System.out.println(error);
        }
        else
            System.out.println("The system was read successfully.");
    }

    @Override
    public void showFlowInformation() {
        Integer choice = getChoiceOfFlow();
        if(choice <= 0)
            return;

        IFlowInformation flowInfo = engine.getFlowInfo(engine.getFlowNames().get(choice - 1));
        System.out.println("\nFlow information:");

        System.out.println("Name: " + flowInfo.name());

        System.out.println("Description: " + flowInfo.description());

        System.out.println("Formal outputs: ");
        flowInfo.formalOutputs().forEach(f->{
            System.out.println("\tName: " + f.getName() + ", Type: " + f.getDataDefinition().getName());
        });

        System.out.println("Read only: " + flowInfo.isReadOnly());

        System.out.println("Steps in flow:");
        flowInfo.steps().forEach(s->{
            System.out.println("\tName: " + s.step().getName() + (s.name().equals(s.step().getName()) ? "" : ", alias: " + s.name()) + ", read only: " + s.step().isReadOnly());
        });

        System.out.println("Open user inputs: ");
        flowInfo.openUserInputs().forEach(input->{
            System.out.println("\tName: " + input.getName() + ", Type: " + input.getDataDefinition().getName() + ", necessity: " + input.getNecessity());
            System.out.println("\tLinked steps: ");
            flowInfo.linkedSteps(input).forEach(step->{
                System.out.println("\t\tName: " + step.step().getName());
            });
            System.out.println();
        });

        System.out.println("Internal outputs: ");
        flowInfo.internalOutputs().forEach(output->{
            System.out.println("\tName: " + output.getName() + ", Type: " + output.getDataDefinition().getName());
            System.out.println("\tProduced by step: " + flowInfo.producer(output).name());
            System.out.println();
        });
    }

    @Override
    public void executeFlow() {
        Integer choice = getChoiceOfFlow();
        if(choice <= 0)
            return;

        // Get required inputs to run the flow
        String flowName = engine.getFlowNames().get(choice - 1);
        ExecutionUserInputs inputs = engine.getExecutionUserInputs(flowName);
        // Read the inputs from the user
        boolean run = false;
        int numInputs = inputs.getOpenUserInputs().size();
        while(!run){
            System.out.println("Free flow inputs: (or enter '0' to exit)");
            for(int i = 0; i < numInputs; i++){
                IDataIO input = inputs.getOpenUserInputs().get(i);
                System.out.println((i + 1) + ") " + input.getUserString()
                        + " (" + inputs.getStep(input).name() + ")"
                        + ", Type: " + input.getDataDefinition().getName()
                        + ", Necessity: " + input.getNecessity());
            }
            if(inputs.validateUserInputs())
                System.out.println(numInputs + 1 + ") Run flow.");

            String input;
            do {
                input = scanner.nextLine();
                try{
                    choice = Integer.parseInt(input);
                    if(inputs.validateUserInputs() && choice == numInputs + 1){
                        run = true;
                    }
                    else if(choice < 0 || choice > inputs.getOpenUserInputs().size())
                        throw new NumberFormatException();
                }catch (NumberFormatException e){
                    System.out.println("Invalid input, please try again.");
                    choice = null;
                }
            }while(choice == null);

            if(choice == 0)
                return;

            if(!run){
                System.out.println("Please enter the value for the selected input:");
                input = scanner.nextLine();
                try {
                    inputs.readUserInput(inputs.getOpenUserInputs().get(choice - 1), input);
                } catch (BadTypeException e) {
                    System.out.println("The input doesn't match the required type!");
                }
            }
        }

        System.out.println("Executing flow...");
        IFlowRunResult result = engine.runFlow(flowName, inputs);
        System.out.println("Flow run info:");
        System.out.println("\tFlow run id: " + result.runId());
        System.out.println("\tFlow name: " + result.name());
        System.out.println("\tFlow execution result flag: " + result.result());
        System.out.println("\tFlow formal outputs:");
        DataParser parser = DataParser.instance();
        for(IDataIO output : result.flowOutputs().keySet()){
            Object value = result.flowOutputs().get(output);
            System.out.println("\t\t" + output.getUserString() + ": ");
            if(value != null){
                System.out.println("\t\t\t" + parser.parse(value).replaceAll("\n","\n\t\t\t"));
            }
            else{
                System.out.println("\t\t\tValue isn't set due to a run-time error in the flow.");
            }
        }
    }

    private IFlowRunResult getPastRunChoice(){
        List<IFlowRunResult> runs = engine.getFlowRuns();

        System.out.println("Please choose one of the following flows by 'Run Id': (or enter '0' to exit)");

        for(IFlowRunResult result : runs) {
            System.out.println("Name: " + result.name()
                    + "\nRun Id: " + result.runId()
                    + "\nStart time: " + DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault()).format(result.startTime()));
            System.out.println();
        }


        List<String> runIds = runs.stream().map(run->run.runId()).collect(Collectors.toList());
        String input;
        do {
            input = scanner.nextLine();
            final String in = input;
            if(input.equals("0"))
                return null;
            if(runIds.contains(input))
                return runs.stream().filter(run->run.runId().equals(in)).findFirst().get();
            System.out.println("Invalid input, please try again.");
            input = null;
        }while(input == null);

        return null;
    }

    @Override
    public void pastRunFullInformation() {
        IFlowRunResult choice = getPastRunChoice();
        if(choice == null)
            return;

        System.out.println("Run Id: " + choice.runId()
                + "\nFlow name: " + choice.name()
                + "\nFlow run result: " + choice.result()
                + "\nFlow run-time: " + choice.duration().toMillis() + " ms"
                + "\nUser inputs:"
        );
        for(IDataIO input : choice.userInputs().keySet()){
            System.out.println("\tName: " + input.getName()
                    + "\n\tType: " + input.getDataDefinition().getName()
                    + "\n\tContent: " + choice.userInputs().get(input)
                    + "\n"
            );
        }
        StatManager manager = engine.getStatistics();
        System.out.println("Steps in flow:");
        for(String uuid : choice.stepRunUUID()){
            IStepRunResult result = manager.getStepRunResult(uuid);
            System.out.println("\tName: " + result.alias()
                    + "\n\tRun-time: " + result.duration().toMillis() + " ms"
                    + "\n\tResult flag: " + result.result()
                    + "\n\tSummary: " + result.summary()
                    + "\n\tLogs:"
            );
            List<Log> stepLogs = Optional.ofNullable(engine.getLogs(uuid)).orElse(new ArrayList<>());
            for(Log log : stepLogs)
                System.out.println("\t\t" + log.toString().replaceAll("\n", "\n\t\t"));
            System.out.println();
        }
    }

    @Override
    public void getSystemStatistics() {
        List<String> flowNames = engine.getFlowNames();
        StatManager statistics = engine.getStatistics();
        System.out.println("Showing system statistics:");
        System.out.println("Flows:");
        for(String flowName : flowNames){
            System.out.println("\tFlow name: " + flowName
                    + "\n\tTimes run: " + statistics.getFlowRunCount(flowName)
                    + "\n\tAverage run-time: " + statistics.getFlowRunAverageTimeMS(flowName).toMillis() + " ms\n"
            );
        }
        List<String> stepNames = Arrays.stream(StepRegistry.values()).map(StepRegistry::getName).collect(Collectors.toList());
        System.out.println("Steps:");
        for(String stepName : stepNames){
            System.out.println("\tStep name: " + stepName
                    + "\n\tTimes run: " + statistics.getStepRunCount(stepName)
                    + "\n\tAverage run-time: " + statistics.getStepRunAverageTimeMS(stepName).toMillis() + " ms\n"
            );
        }
    }

    @Override
    public void createSystemBackup() {
        System.out.println("Please enter the save-file path: (or enter '0' to exit)");
        String path = scanner.nextLine();
        if(path.equals("0"))
            return;

        try {
            engine.writeSystemToFile(path);
            System.out.println("Backup created successfully.");
        } catch (NotAFileException e) {
            System.out.println("Error: path isn't a valid file!");
        } catch (IOException e) {
            System.out.println("Error: can't write to file!");
        }
    }

    @Override
    public void reloadFromBackup() {
        System.out.println("Please enter the save-file path: (or enter '0' to exit)");
        String path = scanner.nextLine();
        if(path.equals("0"))
            return;

        try {
            Boolean result = engine.readSystemFromFile(path);
            if(result)
                System.out.println("Backup restored successfully.");
            else
                System.out.println("Couldn't restore from backup.");
        } catch (NotAFileException e) {
            System.out.println("Error: path isn't a valid file!");
        } catch (IOException e) {
            System.out.println("Error: can't read from file!");
        }
    }

    @Override
    public void exit() {
        System.out.println("Exiting...");
        run = false;
    }
}
