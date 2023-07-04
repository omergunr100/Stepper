package com.main.stepper.server.servlets.history.flow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.engine.executor.implementation.FlowRunResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(name="FlowRunHistoryServlet", urlPatterns = "/history/flow")
public class FlowRunHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        // get requested uuids
        List<UUID> uuids = gson.fromJson(req.getReader(), new TypeToken<List<UUID>>() {}.getType());
        // initialize return list
        List<FlowRunResult> results = new ArrayList<>();
        // check if empty
        if (uuids.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            for (UUID id : uuids) {
                // todo: check if engine has a run with this id, if true add to results, else add to bad list with reason
            }
        }
        gson.toJson(results, new TypeToken<List<FlowRunResult>>() {}.getType(), resp.getWriter());
    }
}
