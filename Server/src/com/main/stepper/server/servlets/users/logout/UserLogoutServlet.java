package com.main.stepper.server.servlets.users.logout;

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
import java.util.Optional;

@WebServlet(name = "UserLogoutServlet", value = "/users/logout")
public class UserLogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Optional<Cookie> name = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("name")).findFirst();
            if (name.isPresent()) {
                // username found
                String username = name.get().getValue();

                // delete user data
                List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
                synchronized (userDataList) {
                    userDataList.stream().filter(user -> user.name().equals(username)).findFirst().ifPresent(user -> user.setLoggedIn(false));
                }
            }
        }
    }
}
