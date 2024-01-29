package com.example.bank.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Logger {

    @Autowired
    private LoggerRepo LoggerRepo;

    public void info(String message, String timestamp, String service, Object payload) {
        Log log = new Log(LogLevel.INFO, message, timestamp, service, payload);
        LoggerRepo.save(log);
    }

    public void warn(String message, String timestamp, String service, Object payload) {
        Log log = new Log(LogLevel.WARN, message, timestamp, service, payload);
        LoggerRepo.save(log);
    }

    public void error(String message, String timestamp, String service, Object payload) {
        Log log = new Log(LogLevel.ERROR, message, timestamp, service, payload);
        LoggerRepo.save(log);
    }
}
