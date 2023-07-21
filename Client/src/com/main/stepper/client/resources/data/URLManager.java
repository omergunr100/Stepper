package com.main.stepper.client.resources.data;

public class URLManager {
    public static final String SERVER_ADDRESS = "http://localhost:8080/stepper";
    public static final String SERVER_HEALTH = SERVER_ADDRESS + "/health";

    public static final String LOGIN = SERVER_ADDRESS + "/users/sign";
    public static final String LOGOUT = SERVER_ADDRESS + "/users/logout";

    public static final String FLOW_RUN_RESULTS = SERVER_ADDRESS + "/history/flow";
    public static final String STEP_RUN_RESULTS = SERVER_ADDRESS + "/history/step";

    public static final String USERS_DATA = SERVER_ADDRESS + "/users/data";

    public static final String FLOW_INFORMATION = SERVER_ADDRESS + "/flow/information";

    public static final String FLOW_EXECUTION = SERVER_ADDRESS + "/run/flow";
}
