package com.main.stepper.server.servlets.availableflows;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.data.implementation.FlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.server.roles.RoleManager;
import com.main.stepper.shared.structures.flow.FlowInfoDTO;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "FlowInformationServlet", value = "/flow/information")
public class FlowInformationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
        // search for user cookie
        Optional<Cookie> name;
        Cookie[] cookies = req.getCookies();
        if (cookies == null)
            name = Optional.empty();
        else
            name = Arrays.stream(cookies).filter(c -> c.getName().equals("name")).findFirst();
        List<FlowInfoDTO> information = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .enableComplexMapKeySerialization()
                .create();
        UserData user = null;
        if (name.isPresent()) {
            // find user object
            List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
            Optional<UserData> userData = userDataList.stream().filter(u -> u.name().equals(name.get().getValue())).findFirst();
            if (userData.isPresent()) {
                // user found
                user = userData.get();
            }
            else {
                // return unauthorized
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }

        // check if admin/manager
        String isAdmin = req.getHeader("isAdmin");
        if ((user != null && user.isManager()) || (isAdmin != null && isAdmin.equals("true"))) {
            // admin/manager found -> ok
            resp.setStatus(HttpServletResponse.SC_OK);
            // return flow information about all flows
            information.addAll(engine.getFlows().stream().map(IFlowDefinition::information).map(IFlowInformation::toDTO).collect(Collectors.toList()));
        }
        else {
            // get flow name parameter
            Set<String> flowNames = gson.fromJson(req.getReader(), new TypeToken<HashSet<String>>(){}.getType());
            resp.setStatus(HttpServletResponse.SC_OK);
            for (String flowName : flowNames) {
                // check if user is manager
                if (RoleManager.uniqueUnionGroup(user.roles()).contains(flowName)) {
                    IFlowInformation flowInfo = engine.getFlowInfo(flowName);
                    if (flowInfo != null) {
                        information.add(flowInfo.toDTO());
                    }
                }
            }
        }
        gson.toJson(information, new TypeToken<ArrayList<FlowInfoDTO>>(){}.getType(), resp.getWriter());
    }
}
