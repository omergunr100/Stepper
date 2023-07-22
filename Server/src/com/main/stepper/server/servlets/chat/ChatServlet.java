package com.main.stepper.server.servlets.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.server.constants.ServletAttributes;
import com.main.stepper.shared.structures.chat.message.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "ChatServlet", value = "/chat")
public class ChatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Message> messages = (List<Message>) req.getServletContext().getAttribute(ServletAttributes.CHAT_RECORDS);
        Gson gson = new Gson();
        gson.toJson(messages, new TypeToken<ArrayList<Message>>(){}.getType(), resp.getWriter());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Instant time = Instant.now();
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Optional<Cookie> name = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("name")).findFirst();
            if (name.isPresent()) {
                // name found read message
                Gson gson = new Gson();
                String str = gson.fromJson(req.getReader(), String.class);
                Message message = new Message(str, time, name.get().getValue());

                List<Message> messages = (List<Message>) req.getServletContext().getAttribute(ServletAttributes.CHAT_RECORDS);
                synchronized (messages) {
                    messages.add(message);
                }

                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
