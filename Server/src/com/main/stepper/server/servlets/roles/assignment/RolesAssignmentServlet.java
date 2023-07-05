package com.main.stepper.server.servlets.roles.assignment;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.Optional;

@WebServlet(name="RolesAssignmentServlet", urlPatterns = "/roles/assign")
public class RolesAssignmentServlet extends HttpServlet {
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
        Gson gson = new Gson();
        String[] strings = gson.fromJson(req.getReader(), String[].class);
        // check if assignment is valid
        if (strings.length != 2 || !(strings[0] instanceof String) || !(strings[1] instanceof String)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // assign values
        String username = (String) strings[0];
        String role = (String) strings[1];
        // find matching user and role
        List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
        List<Role> roles = RoleManager.getRoles();
        Optional<UserData> userData = userDataList.stream().filter(user -> user.name().equals(username)).findFirst();
        Optional<Role> roleData = roles.stream().filter(r -> r.name().equals(role)).findFirst();
        // check if user and role exist
        if (!userData.isPresent() || !roleData.isPresent()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // assign role to user
        if (userData.get().addRole(roleData.get()))
            resp.setStatus(HttpServletResponse.SC_OK);
        else
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        String[] strings = gson.fromJson(req.getReader(), String[].class);
        // check if assignment is valid
        if (strings.length != 2 || strings[0] == null || strings[1] == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // assign values
        String username = (String) strings[0];
        String role = (String) strings[1];
        // find matching user and role
        List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
        Optional<UserData> userData = userDataList.stream().filter(user -> user.name().equals(username)).findFirst();
        Optional<Role> roleData = RoleManager.getRole(role);
        // check if user and role exist
        if (!userData.isPresent() || !roleData.isPresent()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // assign role to user
        if (userData.get().removeRole(roleData.get()))
            resp.setStatus(HttpServletResponse.SC_OK);
        else
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
    }
}
