package com.main.stepper.logger.api;

import com.main.stepper.logger.implementation.data.Log;

import java.util.List;

public interface ILogger {
    void log(String uuid, String message);
    void log(String message);
    List<Log> getLog(String uuid);
    ILogger getSubLogger(String uuid);
    void clear();
}
