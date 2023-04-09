package data.definitions.member;

import java.util.ArrayList;
import java.util.List;

public class Table {
    protected List<String> titles;
    protected List<List<String>> data;

    public Table(String... titles){
        this.titles = new ArrayList<>();
        for(String title : titles)
            this.titles.add(title);
        this.data = new ArrayList<>();
    }
    public Table(List<String> titles){
        this.titles = titles;
        this.data = new ArrayList<>();
    }
    public Table(Table table){
        this.titles = table.titles;
        this.data = table.data;
    }
    public List<String> getTitles(){
        return this.titles;
    }
    public void setTitles(List<String> titles){
        this.titles = titles;
    }
    public void addRow(List<String> row){
        for(int i = 0; i < row.size(); i++)
            if(row.get(i) == null)
                row.set(i,"N\\A");
        this.data.add(row);
    }
    public void removeRow(int index){
        this.data.remove(index);
    }
    public void addColumn(String title){
        this.titles.add(title);
        for(List<String> row : this.data)
            row.add("N\\A");
    }
    public void removeColumn(int index){
        this.titles.remove(index);
        for(List<String> row : this.data)
            row.remove(index);
    }
    public List<String> getRow(int index){
        return this.data.get(index);
    }
    public List<String> getColumn(int index){
        List<String> column = new ArrayList<>();
        for(List<String> row : this.data)
            column.add(row.get(index));
        return column;
    }
}
