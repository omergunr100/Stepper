package com.main.stepper.server.servlets.context;

import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.definition.implementation.ServerEngine;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.FlowRunResult;
import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.flow.definition.api.StepUsageDeclaration;
import com.main.stepper.flow.execution.implementation.FlowExecutionContext;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // initialize the user data list
        sce.getServletContext().setAttribute(ServletAttributes.USER_DATA_LIST, new ArrayList<UserData>());
        // initialize the engine
        sce.getServletContext().setAttribute(ServletAttributes.ENGINE, new ServerEngine());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
