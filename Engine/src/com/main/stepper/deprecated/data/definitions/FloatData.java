package com.main.stepper.deprecated.data.definitions;

public class FloatData extends DataDef<Double> {
    public FloatData(){
        this.data = null;
        this.setUserFriendly(true);
    }

    public Double getData() {
        return data;
    }

    public void setData(Double data) {
        this.data = data;
    }
}
