package com.main.stepper.server.servlets.availableflows;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.server.roles.RoleManager;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "FlowInformationServlet", value = "/flow/information")
public class FlowInformationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
        // search for user cookie
        Cookie[] cookies = req.getCookies();
        Optional<Cookie> name = Arrays.stream(cookies).filter(c -> c.getName().equals("name")).findFirst();
        List<IFlowInformation> information = new ArrayList<>();
        Gson gson = new Gson();
        if (name.isPresent()) {
            // find user object
            List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
            Optional<UserData> userData = userDataList.stream().filter(u -> u.name().equals(name.get().getValue())).findFirst();
            if (userData.isPresent()) {
                // user found
                // get flow name parameter
                List<String> flowNames = gson.fromJson(req.getReader(), new TypeToken<ArrayList<String>>(){}.getType());
                resp.setStatus(HttpServletResponse.SC_OK);
                for (String flowName : flowNames) {
                    // check if user is manager
                    if (userData.get().isManager() || RoleManager.uniqueUnionGroup(userData.get().roles()).contains(flowName)) {
                        IFlowInformation flowInfo = engine.getFlowInfo(flowName);
                        if (flowInfo != null) {
                            information.add(flowInfo);
                        }
                    }
                }
            }
            else {
                // return unauthorized
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        else {
            // check if admin doing debug stuff
            String isAdmin = req.getHeader("isAdmin");
            if (isAdmin != null && isAdmin.equals("true")) {
                // admin found -> ok
                resp.setStatus(HttpServletResponse.SC_OK);
                // return flow information
                List<String> flowNames = gson.fromJson(req.getReader(), new TypeToken<ArrayList<String>>(){}.getType());
                for (String flowName : flowNames) {
                    // check if user is manager
                    IFlowInformation flowInfo = engine.getFlowInfo(flowName);
                    if (flowInfo != null) {
                        information.add(flowInfo);
                    }
                }
            }
            else {
                // return unauthorized
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        gson.toJson(information, new TypeToken<List<IFlowInformation>>(){}.getType(), resp.getWriter());
    }
}
