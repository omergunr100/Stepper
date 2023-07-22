package com.main.stepper.client.resources.fxml.chat;

import com.google.gson.Gson;
import com.main.stepper.client.resources.data.URLManager;
import com.main.stepper.client.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.client.resources.fxml.chat.message.MessageController;
import com.main.stepper.shared.structures.chat.message.Message;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.main.stepper.client.resources.data.PropertiesManager.HTTP_CLIENT;
import static com.main.stepper.client.resources.data.PropertiesManager.chatMessages;

public class ChatController {
    @FXML private VBox messageBox;
    @FXML private TextArea inputArea;
    @FXML private Button sendButton;
    @FXML private ScrollPane scroller;

    public SimpleBooleanProperty isOpen = new SimpleBooleanProperty(true);

    public ChatController() {
    }

    @FXML public void initialize() {
        // force load pre-existing messages
        chatMessages.forEach(this::loadMessage);

        // disable button if no message to send
        inputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                sendButton.setDisable(false);
            }
            else {
                sendButton.setDisable(true);
            }
        });

        // get chat messages and load to message box
        chatMessages.addListener((ListChangeListener<? super Message>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(this::loadMessage);
            }
        });
    }

    private void loadMessage(Message message) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MessageController.class.getResource("Message.fxml"));
        try {
            Parent component = loader.load();
            messageBox.getChildren().add(component);
            MessageController controller = loader.getController();
            controller.loadMessage(message);
            scroller.setVvalue(1.0);
        } catch (IOException ignored) {
        }
    }

    @FXML private void sendMessage() {
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(gson.toJson(inputArea.getText()), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(URLManager.CHAT)
                .put(body)
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> new ErrorPopup("Server unreachable, please try again later"));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> inputArea.clear());
                    }
                    response.close();
                }
            });
        }
    }
}
