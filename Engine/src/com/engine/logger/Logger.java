package com.engine.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private FileWriter writer;
    private SimpleDateFormat format;

    public Logger(String fileName){
        format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
        try {
            writer = new FileWriter("./logs/" + fileName + ".log", true);
        } catch (IOException e) {
        }
    }

    public void log(String message){
        Date now = new Date();
        try {
            writer.write(format.format(now) + ": " + message);
        } catch (IOException e) {
        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
        }
    }
}
