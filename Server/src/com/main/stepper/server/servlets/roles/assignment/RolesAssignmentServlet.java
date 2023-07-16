package com.main.stepper.server.servlets.roles.assignment;

import com.google.gson.Gson;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.server.roles.RoleManager;
import com.main.stepper.shared.structures.roles.Role;
import com.main.stepper.shared.structures.users.UserData;
import com.main.stepper.shared.structures.wrappers.RolesAssignmentWrapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        RolesAssignmentWrapper rolesAssignmentWrapper = gson.fromJson(req.getReader(), RolesAssignmentWrapper.class);

        List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);

        Optional<UserData> first;
        synchronized (userDataList) {
            first = userDataList.stream().filter(user -> user.name().equals(rolesAssignmentWrapper.username())).findFirst();
        }
        if (first.isPresent()) {
            UserData userData = first.get();
            List<Role> collect = rolesAssignmentWrapper.roles().stream().map(role -> {
                Optional<Role> maybeRole = RoleManager.getRole(role);
                if (maybeRole.isPresent())
                    return maybeRole.get();
                return null;
            }).filter(role -> role != null).collect(Collectors.toList());
            userData.updateRoles(collect);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
