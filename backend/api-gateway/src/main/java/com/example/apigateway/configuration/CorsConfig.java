package com.example.apigateway.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private static final Long CORS_MAX_AGE_SECONDS = 3600L;
    private final CorsRoutes corsRoutes;

    @Bean
    public CorsWebFilter corsWebFilter() {
        List<String> allowedOriginPatterns = new ArrayList<>();
        allowedOriginPatterns.add("localhost:4200");

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOriginPatterns(allowedOriginPatterns);
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedMethod("PATCH");

        corsConfig.addAllowedHeader("Content-Type");
        corsConfig.addAllowedHeader("X-Authorization");
        corsConfig.addAllowedHeader("X-Authorization-Refresh");
        corsConfig.addAllowedHeader("X-Platform");
        corsConfig.addAllowedHeader("range");

        corsConfig.setMaxAge(CORS_MAX_AGE_SECONDS);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        for (String route : corsRoutes.getRoutes()) {
            source.registerCorsConfiguration(route, corsConfig);
        }

        return new CorsWebFilter(source);
    }
}