package com.example.apigateway.service;

import com.example.shared.clients.OrderProcessingClient;
import com.example.shared.model.order.OrderDto;
import com.example.shared.model.order.OrderStatus;
import com.example.shared.model.order.OrderStatusUpdateRequest;
import com.example.shared.model.order.ProcessOrderRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderProcessingClientProxy extends ClientProxy {

    private final OrderProcessingClient orderProcessingClientClient;

    private final Logger logger = LoggerFactory.getLogger(OrderProcessingClientProxy.class);

    public Optional<OrderDto> createOrder(ProcessOrderRequest processOrderRequest) {
        try {
            Response<OrderDto> createOrderResponse = execute(() -> orderProcessingClientClient.processOrder(processOrderRequest));
            return handleResponse(createOrderResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<OrderDto> getOrder(String orderId) {
        try {
            Response<OrderDto> createOrderResponse = execute(() -> orderProcessingClientClient.getOrder(orderId));
            return handleResponse(createOrderResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public OrderStatus updateOrderStatus(String orderId, OrderStatusUpdateRequest orderStatusUpdateRequest) {
        try {
            Response<OrderStatus> updateOrderResponse = execute(() -> orderProcessingClientClient.updateOrderStatus(orderId, orderStatusUpdateRequest));
            return handleResponseRaw(updateOrderResponse);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
