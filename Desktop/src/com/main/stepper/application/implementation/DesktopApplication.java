package com.main.stepper.application.implementation;

import com.main.stepper.application.resources.css.CSSRegistry;
import com.main.stepper.application.resources.fxml.root.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class DesktopApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private RootController rootController;

    public DesktopApplication(){
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Stepper");

        FXMLLoader loader = new FXMLLoader();
        URL resource = RootController.class.getResource("Root.fxml");
        loader.setLocation(resource);
        Parent root = loader.load();
        this.rootController = loader.getController();
        rootController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, 840, 520);
        scene.getStylesheets().add(CSSRegistry.class.getResource(CSSRegistry.DEFAULT.getFile().getPath()).toExternalForm());
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
