package com.application.console;

import com.application.IApplication;
import com.engine.Engine;
import com.engine.IEngine;

import java.util.List;
import java.util.Scanner;

public class ConsoleApplication implements IApplication {
    private boolean run = true;
    private IEngine engine;
    private Scanner scanner;

    ConsoleApplication(IEngine engine) {
        this.engine = engine;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        ConsoleApplication application = new ConsoleApplication(new Engine());
        while(application.run)
            application.presentMenu();
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
        List<String> flowNames = engine.getFlowNames();
        if(flowNames.size() == 0){
            System.out.println("There are no flows in the system.");
            return;
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

        if(choice == 0){
            System.out.println("Returning to main menu...");
            return;
        }

        System.out.println(engine.getFlowInfo(flowNames.get(choice - 1)));
    }

    @Override
    public void executeFlow() {
        List<String> flowNames = engine.getFlowNames();
        if(flowNames.size() == 0){
            System.out.println("There are no flows in the system.");
            return;
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

        if(choice == 0){
            System.out.println("Returning to main menu...");
            return;
        }



        System.out.println("Executing flow...");
        engine.runFlow(flowNames.get(choice - 1));
        System.out.println("Flow executed successfully.");
    }

    @Override
    public void pastRunFullInformation() {

    }

    @Override
    public void getSystemStatistics() {

    }

    @Override
    public void exit() {
        System.out.println("Exiting...");
        run = false;
    }
}
