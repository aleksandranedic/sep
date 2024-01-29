package com.example.paymentcryptoservice.logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("logs")
public class Log {
    public LogLevel logLevel;
    public String message;
    public String timestamp;
    public String service;
    public Object payload;
}
