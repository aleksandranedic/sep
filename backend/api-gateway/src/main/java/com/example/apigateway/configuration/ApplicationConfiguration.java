package com.example.apigateway.configuration;

import com.example.apigateway.service.RetrofitFactory;
import com.example.shared.clients.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final AuthServiceConfiguration authServiceConfiguration;
    private final RetrofitFactory retrofitFactory;

    @Bean
    AuthServiceClient getAuthServiceClient() {
        return retrofitFactory.getConfiguredRetrofit(authServiceConfiguration.getUrl())
                .create(AuthServiceClient.class);
    }
}
