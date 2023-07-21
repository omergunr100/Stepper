package com.main.stepper.client.application;

import com.main.stepper.client.resources.css.CSSRegistry;
import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.data.URLManager;
import com.main.stepper.client.resources.fxml.login.LoginScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    public Application(){
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Stepper");

        FXMLLoader loader = new FXMLLoader();
        URL resource = LoginScreenController.class.getResource("LoginScreen.fxml");
        loader.setLocation(resource);
        Parent loginScreen = loader.load();
        LoginScreenController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(loginScreen, 840, 520);
        scene.getStylesheets().add(CSSRegistry.class.getResource(CSSRegistry.DEFAULT.getFile().getPath()).toExternalForm());
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
