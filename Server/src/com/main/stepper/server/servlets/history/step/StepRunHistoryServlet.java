package com.main.stepper.server.servlets.history.step;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.FlowRunResult;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name="StepRunHistoryServlet", urlPatterns = "/history/step")
public class StepRunHistoryServlet extends HttpServlet {
    private List<IStepRunResult> getClient(List<UUID> uuids, String cookie) {
        // check if empty
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }
        else {
            IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
            List<IStepRunResult> stepRunsFromList = engine.getStepRunsFromList(uuids);
            return stepRunsFromList.stream().filter(s -> s.user().equals(cookie)).collect(Collectors.toList());
        }
    }

    private List<IStepRunResult> getAdmin(List<UUID> uuids) {
        // check if empty
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }
        else {
            IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
            return engine.getStepRunsFromList(uuids);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // get requested uuids
        Gson gson = new Gson();
        List<UUID> uuids = gson.fromJson(req.getReader(), new TypeToken<List<UUID>>() {}.getType());
        // get user cookie
        Cookie[] cookies = req.getCookies();
        Optional<Cookie> cookie = Arrays.stream(cookies).filter(c -> c.getName().equals("name")).findFirst();
        // initialize return list
        List<IStepRunResult> results;
        if (cookie.isPresent()) {
            // check if manager or not
            List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
            Optional<UserData> user = userDataList.stream().filter(u -> u.name().equals(cookie.get().getValue())).findFirst();
            resp.setStatus(HttpServletResponse.SC_OK);
            if (user.isPresent()) {
                // check if manager -> if so treat as if admin on this servlet
                if (user.get().isManager()) {
                    results = getAdmin(uuids);
                }
                else {
                    results = getClient(uuids, cookie.get().getValue());
                }
            }
            else {
                results = new ArrayList<>();
            }
        }
        else {
            // check if admin
            String isAdmin = req.getHeader("isAdmin");
            if (isAdmin != null && isAdmin.equals("true")) {
                resp.setStatus(HttpServletResponse.SC_OK);
                results = getAdmin(uuids);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                results = new ArrayList<>();
            }
        }
        gson.toJson(results, new TypeToken<ArrayList<FlowRunResult>>() {}.getType(), resp.getWriter());
    }
}
