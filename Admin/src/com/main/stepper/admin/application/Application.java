package com.main.stepper.admin.application;

import com.main.stepper.admin.resources.css.CSSRegistry;
import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.fxml.root.RootController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        PropertiesManager.primaryStage.set(primaryStage);
        primaryStage.setTitle("Stepper - Administrator");

        FXMLLoader loader = new FXMLLoader();
        URL resource = RootController.class.getResource("Root.fxml");
        loader.setLocation(resource);
        Parent root = loader.load();
        this.rootController = loader.getController();

        Scene scene = new Scene(root, 840, 520);
        scene.getStylesheets().add(CSSRegistry.class.getResource(CSSRegistry.DEFAULT.getFile().getPath()).toExternalForm());
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();

        UpdatePropertiesThread updatePropertiesThread = new UpdatePropertiesThread();
        updatePropertiesThread.setDaemon(true);
        updatePropertiesThread.start();
        PropertiesManager.health.set(false);
    }
}
