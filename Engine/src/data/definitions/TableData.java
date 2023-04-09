package data.definitions;

import data.definitions.member.Table;

import java.util.List;

public class TableData extends DataDef<Table> {
    public TableData(List<String> titles) {
        this.data = new Table(titles);
        this.setUserFriendly(false);
    }
    public List<String> getTitles() {
        return this.data.getTitles();
    }
    public void setTitles(List<String> titles) {
        this.data.setTitles(titles);
    }
    @Override
    public Table getData() {
        return this.data;
    }
    @Override
    public void setData(Table data) {
        this.data = data;
    }
}
