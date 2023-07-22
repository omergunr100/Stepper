package com.main.stepper.client.resources.fxml.root;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.client.resources.fxml.chat.ChatController;
import com.main.stepper.client.resources.fxml.header.loadcss.LoadCSSController;
import com.main.stepper.client.resources.fxml.header.rolesfilter.RolesFilterController;
import com.main.stepper.client.resources.fxml.tabs.executionshistory.tab.ExecutionHistoryScreenController;
import com.main.stepper.client.resources.fxml.tabs.flowsdefinition.FlowsDefinitionController;
import com.main.stepper.client.resources.fxml.tabs.flowsexecution.tab.FlowExecutionController;
import com.main.stepper.shared.structures.roles.Role;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RootController {
    // stage
    private Stage primaryStage;
    // root
    @FXML private GridPane root;
    // header elements
    @FXML private LoadCSSController loadCSSController;
    @FXML private TextField userNameTextField;
    @FXML private CheckBox isManagerCheckBox;
    @FXML private Button openChatButton;
    @FXML private RolesFilterController roleFilterController;
    // tabs
    @FXML private TabPane tabs;
    @FXML private Tab flowsDefinitionTab;
    @FXML private Tab flowsExecutionTab;
    @FXML private Tab executionsHistoryTab;
    // tab screen controllers
    @FXML private FlowsDefinitionController flowsDefinitionController;
    @FXML private FlowExecutionController flowExecutionController;
    @FXML private ExecutionHistoryScreenController flowExecutionHistoryController;
    // chat screen
    private Stage chatStage = null;
    private ChatController chatController = null;

    public RootController() {
    }

    @FXML public void initialize(){
        // listen on server connection loss
        PropertiesManager.health.addListener((observable, oldValue, newValue) -> {
            root.setDisable(!newValue);
        });
        // bind to properties
        userNameTextField.textProperty().bind(PropertiesManager.userName);
        isManagerCheckBox.selectedProperty().bind(PropertiesManager.isManager);
        // set root controller
        loadCSSController.setRootController(this);
        // select initial window
        tabs.getSelectionModel().select(flowsDefinitionTab);
        // on execution flow selection change (if not null go to tab)
        PropertiesManager.executionSelectedFlow.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tabs.getSelectionModel().select(flowsExecutionTab);
            }
        });
    }

    @FXML private void openChat() {
        if (chatController == null || chatController.isOpen.not().get()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ChatController.class.getResource("Chat.fxml"));
            try {
                Parent component = loader.load();
                this.chatController = loader.getController();
                this.chatController.isOpen.set(true);
                this.chatStage = new Stage();
                Scene scene = new Scene(component);
                Bindings.bindContent(scene.getStylesheets(), primaryStage.getScene().getStylesheets());
                this.chatStage.setScene(scene);
                this.chatStage.setTitle("Chat");
                this.chatStage.show();
                this.chatStage.setOnCloseRequest(event -> {
                    this.chatController.isOpen.set(false);
                    this.chatStage = null;
                    Bindings.unbindContent(scene.getStylesheets(), primaryStage.getScene().getStylesheets());
                });
            } catch (IOException ignored) {
            }
        }
        else {
            new ErrorPopup("Chat already open");
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
