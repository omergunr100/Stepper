package com.main.stepper.application.implementation;

import com.main.stepper.application.resources.fxml.root.RootController;
import com.main.stepper.flow.definition.api.IFlowDefinition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        URL resource = getClass().getResource("../resources/fxml/root/Root.fxml");
        loader.setLocation(resource);
        Parent root = loader.load();
        this.rootController = loader.getController();
        rootController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, 840, 520);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
