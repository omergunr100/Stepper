package com.mta.java.stepper.data.implementation.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Relation {
    private List<String> columns;
    private List<TableRow> rows;

    public Relation(List<String> columns) {
        this.columns = columns;
        rows = new ArrayList<>();
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<String> getColumnByRowsOrder(String title){
        List<String> column = new ArrayList<>();
        for(TableRow row : rows)
            column.add(row.getData(title));
        return column;
    }

    public List<String> getRowByColumnsOrder(Integer index){
        List<String> row = new ArrayList<>();
        for(String title : columns)
            row.add(rows.get(index).getData(title));
        return row;
    }

    public Integer getRowCount(){
        return rows.size();
    }

    public Integer getColumnCount(){
        return columns.size();
    }

    public void addRow(List<String> row){
        TableRow tableRow = new TableRow();
        for(int i = 0; i < columns.size(); i++)
            tableRow.addData(columns.get(i), row.get(i));
        rows.add(tableRow);
    }

    private class TableRow {
        private Map<String, String> data;

        public TableRow() {
            data = new HashMap<>();
        }

        public void addData(String title, String value) {
            data.put(title, value);
        }

        public String getData(String title) {
            return data.get(title);
        }
    }
}
