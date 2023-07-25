package com.main.stepper.client.resources.dataview.list;

import com.main.stepper.client.resources.data.PropertiesManager;
import com.main.stepper.client.resources.dataview.relation.RelationViewController;
import com.main.stepper.data.implementation.relation.Relation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class ListViewController {
    @FXML private TableView<Object> table;

    public ListViewController(){
    }

    @FXML public void initialize(){
        table.setPlaceholder(new Label("No data to display"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void reset(){
        table.getItems().clear();
        table.getColumns().clear();
    }

    public void loadList(List list) {
        reset();
        if (list == null || list.isEmpty())
            return;
        TableColumn<Object, String> index = new TableColumn<>("Index");
        index.setPrefWidth(50);
        index.setCellFactory(param -> new TableCell<Object, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : Integer.toString(getIndex() + 1));
            }
        });
        TableColumn<Object, String> column = new TableColumn<>("Items");
        if (list.get(0) instanceof Relation) {
            column.setCellFactory(new Callback<TableColumn<Object, String>, javafx.scene.control.TableCell<Object, String>>() {
                @Override
                public javafx.scene.control.TableCell<Object, String> call(TableColumn<Object, String> param) {
                    final TableCell<Object, String> cell = new TableCell<Object, String>() {
                        final Button button = new Button(Integer.toString(getIndex() + 1));

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            }
                            else {
                                final Relation relation = (Relation) getTableView().getItems().get(getIndex());
                                button.setOnAction(event -> {
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(RelationViewController.class.getResource("RelationView.fxml"));
                                    try {
                                        Parent relationView = loader.load();
                                        RelationViewController controller = loader.getController();
                                        controller.updateTable(relation);
                                        Stage stage = new Stage();
                                        stage.initModality(Modality.APPLICATION_MODAL);
                                        stage.setTitle("Relation view");
                                        Scene scene = new Scene(relationView);
                                        Bindings.bindContent(scene.getStylesheets(), PropertiesManager.primaryStage.get().getScene().getStylesheets());
                                        stage.setScene(scene);
                                        stage.show();
                                    } catch (IOException ignored) {
                                    }
                                });
                                setGraphic(button);
                                setText(null);
                            }
                        }
                    };

                    return cell;
                }
            });
        }
        else {
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().toString()));
        }
        table.getColumns().clear();
        table.getColumns().addAll(index, column);
        table.getItems().addAll(list);
    }
}
