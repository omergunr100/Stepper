package com.main.stepper.logger.implementation.data;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Log implements Serializable {
    private Instant time;
    private String message;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    public Log(String message){
        this.time = Instant.now();
        this.message = message;
    }

    @Override
    public String toString() {
        return "[" + formatter.format(time) + "] " + message;
    }
}
