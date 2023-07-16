package com.main.stepper.server.servlets.context;

import com.main.stepper.engine.definition.implementation.ServerEngine;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.ArrayList;

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
