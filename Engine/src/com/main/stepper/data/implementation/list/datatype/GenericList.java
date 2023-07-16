package com.main.stepper.data.implementation.list.datatype;

import java.util.ArrayList;

public abstract class GenericList<T> extends ArrayList<T> {
    protected GenericList(){
        super();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Integer i = 0; i < this.size(); i++){
            builder
                    .append(i + 1)
                    .append(": ")
                    .append(this.get(i).toString())
                    .append(", ");
        }
        return builder.toString();
    }
}
