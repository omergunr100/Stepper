package com.main.stepper.server.servlets.users.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebServlet(name="UserDataServlet", urlPatterns = "/users/data")
public class UserDataServlet extends HttpServlet {
    /**
     * Admin - This method is used to get the list of all UserDatas.
     * <p>
     * User - This method is used to get the UserData of the user.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getHeader("isAdmin") == null || !req.getHeader("isAdmin").equals("true")) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                Optional<Cookie> name = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("name")).findFirst();
                if (name.isPresent()) {
                    // found username cookie -> try to match a user from UserData list.
                    List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
                    Optional<UserData> match = userDataList.stream().filter(userData -> userData.name().equals(name.get().getValue())).findFirst();
                    if (match.isPresent()) {
                        // found matching user
                        resp.setStatus(HttpServletResponse.SC_OK);
                        Gson gson = new Gson();
                        gson.toJson(match.get(), resp.getWriter());
                    }
                    else {
                        // no matching user
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                else {
                    // no cookies
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            else {
                // no user credentials
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        else {
            // is admin
            resp.setStatus(HttpServletResponse.SC_OK);
            List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
            Gson gson = new Gson();
            gson.toJson(userDataList, new TypeToken<ArrayList<UserData>>(){}.getType(), resp.getWriter());
        }
    }
}
