package com.main.stepper.server.servlets.users.names;

import com.google.gson.Gson;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.users.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "UsernamesServlet", value = "/users/names")
public class UsernamesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserData> userDataList = (List<UserData>) getServletContext().getAttribute(ServletAttributes.USER_DATA_LIST);
        List<String> names;
        synchronized (userDataList) {
            if (!userDataList.isEmpty())
                names = userDataList.stream().map(UserData::name).collect(Collectors.toList());
            else
                names = new ArrayList<>();
        }
        Gson gson = new Gson();
        gson.toJson(names, resp.getWriter());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
