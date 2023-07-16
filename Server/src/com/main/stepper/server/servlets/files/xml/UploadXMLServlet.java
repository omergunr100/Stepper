package com.main.stepper.server.servlets.files.xml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.engine.definition.api.IEngine;
import com.main.stepper.exceptions.xml.XMLException;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.server.roles.RoleManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@WebServlet(name="UploadXMLServlet", urlPatterns = "/files/xml")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 1024 * 1024 * 50, // 50 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class UploadXMLServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getHeader("isAdmin") == null || !req.getHeader("isAdmin").equals("true")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Collection<Part> parts = req.getParts();
        if (parts == null || parts.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            fileContent.append(new Scanner(part.getInputStream()).useDelimiter("\\Z").next());
        }

        IEngine engine = (IEngine) getServletContext().getAttribute(ServletAttributes.ENGINE);
        try {
            List<String> errors = engine.readSystemFromXMLString(fileContent.toString());
            Gson gson = new Gson();
            gson.toJson(errors, new TypeToken<ArrayList<String>>(){}.getType(), resp.getWriter());
            resp.setStatus(HttpServletResponse.SC_OK);
            List<IFlowDefinition> flows = engine.getFlows();
            synchronized (flows) {
                 RoleManager.updateDefaultRoles(flows);
            }
        } catch (XMLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
