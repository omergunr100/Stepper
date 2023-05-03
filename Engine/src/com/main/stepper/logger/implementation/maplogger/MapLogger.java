package com.main.stepper.logger.implementation.maplogger;

import com.main.stepper.logger.api.ILogger;
import com.main.stepper.logger.implementation.data.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLogger implements ILogger {
    private String uuid;
    private final Map<String, List<Log>> logs;

    public MapLogger(){
        uuid = null;
        logs = new HashMap<>();
    }

    public MapLogger(String uuid, Map<String, List<Log>> logs){
        this.uuid = uuid;
        this.logs = logs;
    }

    @Override
    public void log(String uuid, String message) {
        if(!logs.containsKey(uuid))
            logs.put(uuid, new ArrayList<>());
        logs.get(uuid).add(new Log(message));
    }

    @Override
    public void log(String message) {
        if(!logs.containsKey(uuid))
            logs.put(uuid, new ArrayList<>());
        logs.get(uuid).add(new Log(message));
    }

    @Override
    public List<Log> getLog(String uuid) {
        return logs.get(uuid);
    }

    @Override
    public ILogger getSubLogger(String uuid) {
        return new MapLogger(uuid, logs);
    }

    @Override
    public void clear() {
        logs.clear();
    }
}
