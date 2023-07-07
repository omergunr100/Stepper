package com.main.stepper.server.servlets.history.flow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.engine.executor.api.IFlowRunResult;
import com.main.stepper.engine.executor.implementation.FlowRunResult;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
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

@WebServlet(name="FlowRunHistoryServlet", urlPatterns = "/history/flow")
public class FlowRunHistoryServlet extends HttpServlet {
    private List<FlowRunResultDTO> getClient(String cookie) {
        IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
        List<IFlowRunResult> flowRunsFromList = engine.getFlowRuns();
        synchronized (flowRunsFromList) {
            return flowRunsFromList.stream().filter(f -> f.user().equals(cookie)).map(IFlowRunResult::toDTO).collect(Collectors.toList());
        }
    }

    private List<FlowRunResultDTO> getAdmin() {
        IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
        synchronized (engine.getFlowRuns()) {
            return engine.getFlowRuns().stream().map(IFlowRunResult::toDTO).collect(Collectors.toList());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // get user cookie
        Cookie[] cookies = req.getCookies();
        Optional<Cookie> cookie = Arrays.stream(cookies).filter(c -> c.getName().equals("name")).findFirst();
        // initialize return list
        List<FlowRunResultDTO> results;
        if (cookie.isPresent()) {
            // check if manager or not
            List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
            Optional<UserData> user = userDataList.stream().filter(u -> u.name().equals(cookie.get().getValue())).findFirst();
            resp.setStatus(HttpServletResponse.SC_OK);
            if (user.isPresent()) {
                // check if manager -> if so treat as if admin on this servlet
                if (user.get().isManager()) {
                    results = getAdmin();
                }
                else {
                    results = getClient(cookie.get().getValue());
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
                results = getAdmin();
            }
            else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                results = new ArrayList<>();
            }
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        gson.toJson(results, new TypeToken<ArrayList<FlowRunResultDTO>>() {}.getType(), resp.getWriter());
    }
}
