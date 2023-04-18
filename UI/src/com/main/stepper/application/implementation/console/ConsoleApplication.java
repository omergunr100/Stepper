package com.main.stepper.application.implementation.console;

import com.main.stepper.application.api.IApplication;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.definition.implementation.Engine;
import com.main.stepper.engine.executor.api.IFlowRunResult;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleApplication implements IApplication {
    public static void main(String[] args) {
        ConsoleApplication application = new ConsoleApplication(new Engine());
        while(application.run)
            application.presentMenu();
    }

    private boolean run = true;
    private final IEngine engine;
    private Scanner scanner;

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
        Integer choice = null;
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
        System.out.println("6) Exit.");

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
        List<String> errors = engine.readSystemFromXML(path);
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

        System.out.println(engine.getFlowInfo(engine.getFlowNames().get(choice - 1)));
    }

    @Override
    public void executeFlow() {
        Integer choice = getChoiceOfFlow();
        if(choice <= 0)
            return;

        System.out.println("Executing flow...");
        IFlowRunResult result = engine.runFlow(engine.getFlowNames().get(choice - 1));
        // TODO: present the user with the results of the run.
        System.out.println("Flow executed successfully.");
    }

    @Override
    public void pastRunFullInformation() {
        System.out.println("Please enter the UUID of the run:");
        String uuid = scanner.nextLine();
        IFlowRunResult result = engine.getFlowRunInfo(UUID.fromString(uuid));
        if(result == null)
            System.out.println("No such run was found.");
        else
            System.out.println(result);
    }

    @Override
    public void getSystemStatistics() {
        // TODO: implement this method.
    }

    @Override
    public void exit() {
        System.out.println("Exiting...");
        run = false;
    }
}
