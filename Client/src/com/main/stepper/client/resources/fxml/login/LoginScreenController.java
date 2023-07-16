package com.main.stepper.client.resources.fxml.login;

import com.google.gson.Gson;
import com.main.stepper.client.application.UpdatePropertiesThread;
import com.main.stepper.client.resources.css.CSSRegistry;
import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.data.URLManager;
import com.main.stepper.client.resources.fxml.root.RootController;
import com.main.stepper.shared.structures.users.UserData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

import static com.main.stepper.client.resources.data.PropertiesManager.HTTP_CLIENT;

public class LoginScreenController {
    @FXML private TextField userNameTextField;
    @FXML private Button loginButton;
    @FXML private TextField errorTextField;

    private Stage primaryStage;

    public LoginScreenController() {
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML public void initialize(){
        // Disable login button if username is empty
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                loginButton.setDisable(false);
            } else {
                loginButton.setDisable(true);
            }
        });
    }

    @FXML private void login(){
        HttpUrl url = HttpUrl
                .parse(URLManager.LOGIN)
                .newBuilder()
                .addQueryParameter("name", userNameTextField.getText()).build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    showError("Server is not responding, try again later.");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        UserData userData = gson.fromJson(response.body().string(), UserData.class);
                        // update user information
                        PropertiesManager.userName.set(userData.name());
                        PropertiesManager.isManager.set(userData.isManager());
                        PropertiesManager.roles.setAll(userData.roles());
                        PropertiesManager.flowExecutionHistory.setAll(userData.flowExecutionHistory());
                        // continue to main screen
                        Platform.runLater(() -> {
                            primaryStage.setTitle("Stepper");

                            FXMLLoader loader = new FXMLLoader();
                            URL resource = RootController.class.getResource("Root.fxml");
                            loader.setLocation(resource);
                            Parent root = null;
                            try {
                                root = loader.load();
                                RootController rootController = loader.getController();
                                rootController.setPrimaryStage(primaryStage);

                                Scene scene = new Scene(root, 840, 520);
                                scene.getStylesheets().add(CSSRegistry.class.getResource(CSSRegistry.DEFAULT.getFile().getPath()).toExternalForm());
                                primaryStage.setOnCloseRequest(event -> System.exit(0));
                                primaryStage.setScene(scene);
                                primaryStage.show();
                            } catch (IOException ignored) {
                                ignored.printStackTrace();
                            }
                        });

                        // start update thread
                        UpdatePropertiesThread updatePropertiesThread = new UpdatePropertiesThread();
                        updatePropertiesThread.setDaemon(true);
                        updatePropertiesThread.start();
                    } else {
                        showError("Username already taken.");
                        response.close();
                    }
                }
            });
        }
    }

    private void showError(String error) {
        errorTextField.setText(error);
        errorTextField.setVisible(true);
    }
}
