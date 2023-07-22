package com.main.stepper.client.resources.fxml.chat.message;

import com.main.stepper.shared.structures.chat.message.Message;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessageController {
    @FXML private TextArea messageArea;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss").withZone(ZoneId.systemDefault());

    public MessageController() {
    }

    @FXML public void initialize() {
    }

    public void loadMessage(Message message) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatter.format(message.timestamp()));
        sb.append(" - @");
        sb.append(message.user());
        sb.append(":\n");
        sb.append(message.message());
        messageArea.setText(sb.toString());
    }
}
