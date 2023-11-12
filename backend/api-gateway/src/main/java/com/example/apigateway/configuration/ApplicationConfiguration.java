package com.example.apigateway.configuration;

import com.example.apigateway.service.RetrofitFactory;
import com.example.shared.clients.BackofficeClient;
import com.example.shared.clients.OrderProcessingClient;
import com.example.shared.clients.SearchOrdersClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final BackOfficeConfiguration backOfficeConfiguration;
    private final OrderProcessingConfiguration orderProcessingConfiguration;
    private final SearchOrdersConfiguration searchOrdersConfiguration;
    private final RetrofitFactory retrofitFactory;

    @Bean
    BackofficeClient getBackOfficeClient() {
        return retrofitFactory.getConfiguredRetrofit(backOfficeConfiguration.getUrl())
                .create(BackofficeClient.class);
    }

    @Bean
    OrderProcessingClient getOrderProcessingClient() {
        return retrofitFactory.getConfiguredRetrofit(orderProcessingConfiguration.getUrl())
                .create(OrderProcessingClient.class);
    }

    @Bean
    SearchOrdersClient getSearchOrdersClient() {
        return retrofitFactory.getConfiguredRetrofit(searchOrdersConfiguration.getUrl())
                .create(SearchOrdersClient.class);
    }

}
