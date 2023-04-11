package com.engine;

import com.engine.flows.Flow;
import com.engine.xml.validation.Validator;

import java.util.ArrayList;
import java.util.List;

public final class Engine {
    public static void main(String[] args) {
        Engine engine = new Engine();
    }
    private List<Flow> flows;
    public Engine(){
        // TODO: Read file info from user
        Validator validator = Validator.getInstance("C:\\test\\xml\\ex1.xml");
        if(validator.validate()){
            for(String error : validator.getErrors())
                System.out.println(error);
        }

        this.flows = new ArrayList<>();
    }
    public void readSystemFromFile(String path){

    }
}
