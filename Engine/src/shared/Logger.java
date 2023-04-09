package shared;

import flows.Flow;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private FileWriter writer;
    private static Logger logger;
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

    private Logger(){
        Date now = new Date();
        try {
            this.writer = new FileWriter("./" + now + ".log");
        } catch (IOException e) {
        }
    }
    public static void init(){
        logger = new Logger();
    }

    public static void log(String message){
        Date now = new Date();
        try {
            logger.writer.write(format.format(now) + ": " + message);
        } catch (IOException e) {
        }
    }

    public static void logFlow(Flow flow){
        Date now = new Date();
        try {
            logger.writer.write(format.format(now) + ": " + flow);
        } catch (IOException e) {
        }
    }

    public static void close(){
        try {
            logger.writer.close();
        } catch (IOException e) {
        }
    }
}
