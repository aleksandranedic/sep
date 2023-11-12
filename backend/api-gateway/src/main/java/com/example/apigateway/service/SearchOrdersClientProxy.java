package com.example.apigateway.service;

import com.example.shared.clients.SearchOrdersClient;
import com.example.shared.model.order.OrderDto;
import com.example.shared.model.order.SearchOrderDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SearchOrdersClientProxy extends ClientProxy {

    private final SearchOrdersClient searchOrdersClient;
    private final Logger logger = LoggerFactory.getLogger(SearchOrdersClientProxy.class);

    public void createOrder(SearchOrderDTO searchOrderDTO) {
        try {
            Response<OrderDto> createOrderResponse = execute(() -> searchOrdersClient.createOrder(searchOrderDTO));
            if(handleResponse(createOrderResponse).isPresent())
                System.out.println(handleResponse(createOrderResponse));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
