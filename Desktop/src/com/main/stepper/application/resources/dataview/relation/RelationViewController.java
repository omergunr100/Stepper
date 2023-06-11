package com.main.stepper.application.resources.dataview.relation;

import com.main.stepper.data.implementation.relation.Relation;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class RelationViewController {
    @FXML private TableView<List<String>> table;
    public RelationViewController() {
    }

    @FXML public void initialize() {
        table.setPlaceholder(new Label("No data to display"));
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void reset() {
        table.getItems().clear();
        table.getColumns().clear();
    }

    public void updateTable(Relation relation) {
        // reset table
        reset();
        // create table columns
        List<String> columns = relation.getColumns();
        for(int i = 0; i < columns.size(); i++) {
            TableColumn<List<String>, String> column = new TableColumn<>(columns.get(i));
            final int j = i;
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
            table.getColumns().add(column);
        }
        // add rows
        for(int i = 0; i < relation.getRowCount(); i++)
            table.getItems().add(relation.getRowByColumnsOrder(i));
    }
}
