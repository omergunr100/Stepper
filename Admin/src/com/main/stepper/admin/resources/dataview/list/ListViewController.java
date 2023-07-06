package com.main.stepper.admin.resources.dataview.list;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class ListViewController {
    @FXML private TableView<List> table;

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
        for(Integer i = 1; i <= list.size(); i++) {
            TableColumn<List, String> column = new TableColumn<>(i.toString());
            final Integer j = i - 1;
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            TextField field = new TextField();
            field.setText(list.get(j).toString());
            column.setMinWidth(field.getLayoutBounds().getWidth());
            table.getColumns().add(column);
        }
        table.getItems().add(list);
        table.fixedCellSizeProperty().set(35);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(table.getItems().size()).add(30));
    }
}
