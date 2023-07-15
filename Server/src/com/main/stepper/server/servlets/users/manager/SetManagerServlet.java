package com.main.stepper.server.servlets.users.manager;

import com.google.gson.Gson;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name="SetManagerServlet", urlPatterns = "/users/manager")
public class SetManagerServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String isAdmin = req.getHeader("isAdmin");
        if (isAdmin != null && isAdmin.equals("true")) {
            Gson gson = new Gson();
            UserData userData = gson.fromJson(req.getReader(), UserData.class);
            List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
            synchronized (userDataList) {
                Optional<UserData> first = userDataList.stream().filter(user -> user.name().equals(userData.name())).findFirst();
                if (first.isPresent()) {
                    first.get().setManager(userData.isManager());
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
