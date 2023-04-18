package com.main.stepper.application.api;

public interface IApplication {
    /**
     * Presents the main menu to the user
     */
    void presentMenu();

    /**
     * Reads a file path String from the user.
     * Tries to read the system from said file.
     * If the operation fails the user is notified about the errors.
     */
    void readSystemFromFile();

    /**
     * Shows the names of all the flows in the system.
     * The user can choose to see more information about a specific flow.
     * (or return to the main menu)
     */
    void showFlowInformation();

    /**
     * Shows the names of all flows in the system.
     * The user can choose to run a specific flow.
     * (or return to the main menu)
     *
     * If the flow has free inputs, they will be presented (as mandatory or optional),
     * and the user will be asked to fill them.
     * The process of filling inputs will continue until the user chooses to continue.
     *
     * At the end of execution the following will be presented:
     * 1) Run UUID.
     * 2) The flow's name.
     * 3) The flow's end flag (Success\Warning\Failure).
     * 4) Flow's formal outputs. (User string title, value)
     */
    void executeFlow();

    /**
     * Shows the list of past runs to the user to choose from. (in order from most to least recent)
     * Each run will be presented with the following information:
     * 1) Flow name.
     * 2) UUID
     * 3) Time of execution. (HH:MM:SS)
     *
     * The user can choose to see more information about a specific run.
     * (specified in spec sheet)
     *
     * The user may also choose to return to main menu.
     */
    void pastRunFullInformation();

    /**
     * For each flow in the system, the following information will be presented:
     * 1) Number of times the flow was executed.
     * 2) Average execution time (ms).
     *
     * For each step in the system, the following information will be presented:
     * 1) Number of times the step was executed.
     * 2) Average execution time (ms).
     */
    void getSystemStatistics();

    /**
     * Exits the application.
     */
    void exit();
}
