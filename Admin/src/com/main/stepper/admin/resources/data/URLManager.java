package com.main.stepper.admin.resources.data;

public class URLManager {
    // Base address
    public static final String SERVER_ADDRESS = "http://localhost:8080/stepper";

    // Is admin spot available
    public static final String ADMIN = SERVER_ADDRESS + "/admin";

    // Is server alive
    public static final String SERVER_HEALTH = SERVER_ADDRESS + "/health";

    // File upload (xml)
    public static final String XML_FILE_UPLOAD = SERVER_ADDRESS + "/files/xml";

    // All roles
    public static final String ROLES_MANAGEMENT = SERVER_ADDRESS + "/roles";
    // All users
    public static final String USERS_DATA = SERVER_ADDRESS + "/users/data";
    // Set user manager
    public static final String SET_MANAGER = SERVER_ADDRESS + "/users/manager";

    // All flows
    public static final String FLOW_INFORMATION = SERVER_ADDRESS + "/flow/information";

    // history of flow runs
    public static final String FLOW_RUN_RESULTS = SERVER_ADDRESS + "/history/flow";
    // history of step runs
    public static final String STEP_RUN_RESULTS = SERVER_ADDRESS + "/history/step";

    // Roles assignment
    public static final String ROLES_ASSIGNMENT = SERVER_ADDRESS + "/roles/assign";
    public static final String STEP_DEFINITIONS = SERVER_ADDRESS + "/step/definitions";
}
