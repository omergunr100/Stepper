package data.definitions;

public class NumberData extends DataDef<Integer> {
    public NumberData(){
        this.data = null;
        this.setUserFriendly(true);
    }
    public NumberData(Integer data){
        this.data = data;
        this.setUserFriendly(true);
    }
    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
