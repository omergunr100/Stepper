package com.main.stepper.server.servlets.roles.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.server.roles.RoleManager;
import com.main.stepper.shared.structures.users.UserData;
import com.main.stepper.shared.structures.roles.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name="RolesServlet", urlPatterns = "/roles")
public class RolesServlet extends HttpServlet {
    // override to ensure admin status
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
        gson.toJson(RoleManager.getRoles(), new TypeToken<ArrayList<Role>>(){}.getType(), resp.getWriter());
    }

    /**
     * This method is used to change an existing role or define a brand-new role.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Role role = gson.fromJson(req.getReader(), Role.class);
        gson.toJson(role.isLocal(), resp.getWriter());
        Optional<Role> existing = RoleManager.getRole(role.name());
        if (existing.isPresent()) {
            Role existingRole = existing.get();
            existingRole.update(role);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            role.setLocal(false);
            boolean result = RoleManager.addRole(role);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    /**
     * This method is used to delete an existing role.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        String name = gson.fromJson(req.getReader(), String.class);
        List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
        List<UserData> collected = userDataList.stream().filter(userData -> userData.roles().stream().anyMatch(role -> role.name().equals(name))).collect(Collectors.toList());
        if (collected.isEmpty()) {
            boolean result = RoleManager.removeRole(name);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        else {
            // if there are users with this role
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        gson.toJson(collected, new TypeToken<ArrayList<UserData>>(){}.getType(), resp.getWriter());
    }
}
