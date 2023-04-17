package com.mta.java.stepper.logger.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLogger {
    private File file;
    private FileWriter writer;
    private SimpleDateFormat format;

    public FileLogger(String fileName){
        format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
        file = new File("logs\\" + fileName + ".log");
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }

        try {
            writer = new FileWriter(file, true);
        } catch (IOException e) {
        }
    }

    public FileLogger(){
        this(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US).format(new Date()));
    }

    public void log(String message){
        Date now = new Date();
        try {
            writer.write(format.format(now) + ": " + message + "\n");
            writer.flush();
        } catch (IOException e) {
        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
        }
    }

    public File getFile(){
        return file;
    }
}
