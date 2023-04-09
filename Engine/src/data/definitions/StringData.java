package data.definitions;

public class StringData extends DataDef<String> {
    public StringData(){
        this.data = null;
        this.setUserFriendly(true);
    }
    public StringData(String data){
        this.data = data;
        this.setUserFriendly(true);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
