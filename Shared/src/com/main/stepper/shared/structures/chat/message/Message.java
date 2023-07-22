package com.main.stepper.shared.structures.chat.message;

import java.time.Instant;
import java.util.Objects;

public class Message {
    private String message;
    private Instant timestamp;
    private String user;

    public Message(String message, Instant timestamp, String user) {
        this.message = message;
        this.timestamp = timestamp;
        this.user = user;
    }

    public String message() {
        return message;
    }

    public Instant timestamp() {
        return timestamp;
    }

    public String user() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (!Objects.equals(message, message1.message)) return false;
        if (!Objects.equals(timestamp, message1.timestamp)) return false;
        return Objects.equals(user, message1.user);
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
