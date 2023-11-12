package com.example.apigateway.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClientConfiguration {
    private final String PROTOCOL = "http";

    private String host;
    private String port;

    public String getUrl(){
        return  String.format("%s://%s:%s", PROTOCOL, host, port);
    }
}
