package com.example.apigateway.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Component
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "routes-to-add-to-cors-config")
public class CorsRoutes {
    private Set<String> routes;
}
