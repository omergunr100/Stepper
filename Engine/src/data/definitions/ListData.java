package data.definitions;

import javafx.util.Pair;

import java.util.ArrayList;

public class ListData<T extends DataDef> extends DataDef<ArrayList<T>> {
    public ListData() {
        this.data = new ArrayList<>();
        this.setUserFriendly(false);
    }

    public ArrayList<T> getData(){
        return data;
    }

    public void setData(ArrayList<T> data){
        this.data = data;
    }
    public void setData(int index, T value){
        if(index == 0){
            data.add(value);
        }
        else if(index > data.size()){
            throw new IndexOutOfBoundsException();
        }
        else{
            data.set(index - 1, value);
        }
    }
    public int size(){
        return data.size();
    }
    public void add(T value){
        data.add(value);
    }
}
