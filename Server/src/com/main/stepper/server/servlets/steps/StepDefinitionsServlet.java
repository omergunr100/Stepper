package com.main.stepper.server.servlets.steps;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.shared.structures.step.StepDefinitionDTO;
import com.main.stepper.step.definition.StepRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "StepDefinitionsServlet", value = "/step/definitions")
public class StepDefinitionsServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getHeader("isAdmin") == null || !req.getHeader("isAdmin").equals("true")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        List<StepDefinitionDTO> collected = Arrays.stream(StepRegistry.values()).map(StepRegistry::toDTO).collect(Collectors.toList());
        gson.toJson(collected, new TypeToken<List<StepDefinitionDTO>>(){}.getType(), resp.getWriter());
    }
}
