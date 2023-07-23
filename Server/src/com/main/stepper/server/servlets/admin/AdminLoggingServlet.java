package com.main.stepper.server.servlets.admin;

import com.google.gson.Gson;
import com.main.stepper.server.constants.ServletAttributes;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminLoggingServlet", value = "/admin")
public class AdminLoggingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Boolean isAdminUp = (Boolean) getServletContext().getAttribute(ServletAttributes.IS_ADMIN_UP);
        Gson gson = new Gson();
        gson.toJson(!isAdminUp, resp.getWriter());
        if (!isAdminUp)
            getServletContext().setAttribute(ServletAttributes.IS_ADMIN_UP, true);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().setAttribute(ServletAttributes.IS_ADMIN_UP, false);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getHeader("isAdmin") == null || !req.getHeader("isAdmin").equals("true")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        super.service(req, resp);
    }
}
