package com.test;

import com.main.stepper.application.resources.dataview.list.ListViewController;
import com.main.stepper.application.resources.dataview.relation.RelationViewController;
import com.main.stepper.application.resources.fxml.tabs.statistics.tab.StatisticsScreenController;
import com.main.stepper.data.implementation.relation.Relation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL resource = StatisticsScreenController.class.getResource("StatisticsScreen.fxml");
        loader.setLocation(resource);
        Parent root = loader.load();
        StatisticsScreenController controller = loader.getController();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
