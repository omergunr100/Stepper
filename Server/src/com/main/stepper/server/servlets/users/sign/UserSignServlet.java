package com.main.stepper.server.servlets.users.sign;

import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet(name="UserSignServlet", urlPatterns = "/users/sign")
public class UserSignServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getReader().readLine();
        Cookie[] cookies = req.getCookies();
        // get the users list
        List<UserData> users = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
        if (users.stream().anyMatch(userData -> userData.name().equals(name))) {
            // if the user already exists check if he has the matching cookie (returning user) else return conflict
            if (cookies != null && Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals("name") && cookie.getValue().equals(name)))
                resp.setStatus(HttpServletResponse.SC_OK);
            else
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            // give him the matching cookie and add him to the list
            users.add(new UserData(name));
            resp.addCookie(new Cookie("name", name));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
