package com.main.stepper.application.resources.dataview.list;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ListViewController {
    @FXML private TableView<List> table;

    public ListViewController(){
    }

    @FXML public void initialize(){
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void reset(){
        table.getItems().clear();
        table.getColumns().clear();
    }

    public void loadList(List list) {
        reset();
        for(Integer i = 1; i <= list.size(); i++) {
            TableColumn<List, String> column = new TableColumn<>(i.toString());
            final Integer j = i - 1;
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            table.getColumns().add(column);
        }
        table.getItems().add(list);
    }
}
