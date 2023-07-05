package com.main.stepper.server.servlets.context;

import com.main.stepper.engine.definition.implementation.ServerEngine;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // initialize the user data list
        // todo: remove debug user
        List<UserData> userDataList = Arrays.stream(new UserData[]{
                new UserData("test")
        }).collect(Collectors.toList());
        sce.getServletContext().setAttribute(ServletAttributes.USER_DATA_LIST, userDataList);
        // initialize the engine
        sce.getServletContext().setAttribute(ServletAttributes.ENGINE, new ServerEngine());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
