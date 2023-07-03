package com.main.stepper.server.servlets.roles;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.server.roles.RoleManager;
import com.main.stepper.shared.structures.roles.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name="RolesServlet", urlPatterns = "/roles")
public class RolesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        gson.toJson(RoleManager.getRoles(), new TypeToken<List<Role>>(){}.getType(), resp.getWriter());
    }

    /**
     *
     * This method is used to define a brand-new role.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Role newRole = gson.fromJson(req.getReader(), Role.class);
        boolean result = RoleManager.addRole(newRole);
        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * This method is used to delete an existing role.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getReader().readLine();
        boolean result = RoleManager.removeRole(name);
        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
