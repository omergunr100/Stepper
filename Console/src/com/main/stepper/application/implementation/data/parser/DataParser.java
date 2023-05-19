package com.main.stepper.application.implementation.data.parser;

public class DataParser {
    private static DataParser instance = null;

    private DataParser(){
    }

    public static DataParser instance(){
        if(instance == null)
            instance = new DataParser();
        return instance;
    }

    public String parse(Object data){
        return data.toString();
    }
}
