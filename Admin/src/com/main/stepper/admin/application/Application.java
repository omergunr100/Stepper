package com.main.stepper.admin.application;

import com.google.gson.Gson;
import com.main.stepper.admin.resources.css.CSSRegistry;
import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.admin.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.admin.resources.fxml.root.RootController;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    private RootController rootController;

    public Application(){
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // check if admin is already up before starting
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLManager.ADMIN)
                .addHeader("isAdmin", "true")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            new ErrorPopup("Server unreachable, try again later.");
            return;
        }
        Gson gson = new Gson();
        Boolean status = gson.fromJson(response.body().string(), Boolean.class);
        if (!status) {
            new ErrorPopup("Admin already up.");
            return;
        }

        // start admin app
        PropertiesManager.primaryStage.set(primaryStage);
        primaryStage.setTitle("Stepper - Administrator");

        FXMLLoader loader = new FXMLLoader();
        URL resource = RootController.class.getResource("Root.fxml");
        loader.setLocation(resource);
        Parent root = loader.load();
        this.rootController = loader.getController();

        Scene scene = new Scene(root, 840, 520);
        scene.getStylesheets().add(CSSRegistry.class.getResource(CSSRegistry.DEFAULT.getFile().getPath()).toExternalForm());
        primaryStage.setOnCloseRequest(event -> {
            // inform server of closing
            Request closeReq = new Request.Builder()
                    .addHeader("isAdmin", "true")
                    .delete()
                    .url(URLManager.ADMIN)
                    .build();
            try {
                Response closeRes = client.newCall(closeReq).execute();
            } catch (IOException ignored) {
            }
            // close http client executor service threads
            PropertiesManager.HTTP_CLIENT.dispatcher().executorService().shutdown();
            // shutdown
            System.exit(0);
        });
        primaryStage.setScene(scene);
        primaryStage.show();

        UpdatePropertiesThread updatePropertiesThread = new UpdatePropertiesThread();
        updatePropertiesThread.setDaemon(true);
        updatePropertiesThread.start();
        PropertiesManager.health.set(false);
    }
}
