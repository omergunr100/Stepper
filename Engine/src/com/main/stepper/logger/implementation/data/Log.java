package com.main.stepper.logger.implementation.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Log {
    private String message;
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

    public Log(String message){
        this.message = format.format(new Date()) + " - " + message;
    }

    @Override
    public String toString() {
        return message;
    }
}
