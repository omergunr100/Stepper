package data.definitions;

public abstract class DataDef<T> implements HasData<T> {
    protected boolean userFriendly;
    protected T data;
    public boolean isUserFriendly() {
        return userFriendly;
    }
    public void setUserFriendly(boolean userFriendly) {
        this.userFriendly = userFriendly;
    }
}
