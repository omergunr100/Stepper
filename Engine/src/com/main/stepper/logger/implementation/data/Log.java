package com.main.stepper.logger.implementation.data;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Log {
    private Instant time;
    private String message;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public Log(String message){
        this.time = Instant.now();
        this.message = message;
    }

    @Override
    public String toString() {
        return "[" + formatter.format(time) + "] " + message;
    }
}
