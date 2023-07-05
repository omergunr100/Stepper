package com.main.stepper.server.servlets.run;

import com.google.gson.Gson;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.implementation.ExecutionUserInputs;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebServlet(name="RunFlowServlet", urlPatterns = "/run/flow")
public class RunFlowServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // get user cookie
        Cookie[] cookies = req.getCookies();
        Optional<Cookie> name = Arrays.stream(cookies).filter(c -> c.getName().equals("name")).findFirst();
        // get flow name parameter
        String flowName = req.getParameter("flowName");
        // get execution user inputs
        Gson gson = new Gson();
        ExecutionUserInputs executionUserInputs = gson.fromJson(req.getReader(), ExecutionUserInputs.class);

        if (flowName == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // run flow
        UUID uuid = null;
        if (name.isPresent()) {
            // get user data
            List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
            Optional<UserData> user = userDataList.stream().filter(u -> u.name().equals(name)).findFirst();
            // check if user exists
            if (!user.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            // check if user is authorized to run this flow
            if (!user.get().isManager() && !RoleManager.uniqueUnionGroup(user.get().roles()).contains(flowName)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            // get engine
            IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
            uuid = engine.runFlow(name.get().getValue(), flowName, executionUserInputs);
            if (uuid == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_OK);
                gson.toJson(uuid, resp.getWriter());
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
