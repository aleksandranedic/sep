package com.example.authservice.logger;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity()
public class Log {
    public LogLevel logLevel;
    public String message;
    public String timestamp;
    public String service;
    public Object payload;
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Log(LogLevel lvl, String msg, String tmp, String service, Object obj) {
        this.logLevel = lvl;
        this.message = msg;
        this.timestamp = tmp;
        this.service = service;
        this.payload = obj;
    }
}
