package com.example.apigateway.controller;

import com.example.apigateway.service.*;
import com.example.shared.model.order.*;
import com.example.shared.model.restaurant.RestaurantDto;
import com.example.shared.model.user.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-processing")
@RequiredArgsConstructor
public class OrderProcessingController {

    private final BackOfficeClientProxy backOfficeClientProxy;
    private final OrderProcessingClientProxy orderProcessingClientProxy;
    private final OrderProcessingUtil orderProcessingUtil;
    private final SearchOrdersClientProxy searchOrdersClientProxy;
    private final RabbitMQSender rabbitMQSender;

    @PostMapping("/order")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) throws IOException, URISyntaxException, InterruptedException {
        Optional<UserDto> userOptional = backOfficeClientProxy.getUser(createOrderRequest.getUserId());
        Optional<RestaurantDto> restaurantOptional = backOfficeClientProxy.getRestaurant(createOrderRequest.getRestaurantId());
        boolean dataIsPresent = userOptional.isPresent() && restaurantOptional.isPresent();
        if (dataIsPresent) {
            List<MenuItemOrderDto> menuItems = orderProcessingUtil.convertToOrderMenuItems(createOrderRequest.getMenuItems(), restaurantOptional.get());
            System.out.println(menuItems.size());
            if (menuItems.size() > 0)
                return processOrder(userOptional.get(), restaurantOptional.get(), menuItems);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String id) {
        return ResponseEntity.of(orderProcessingClientProxy.getOrder(id));
    }

    private ResponseEntity<OrderDto> processOrder(UserDto user, RestaurantDto
            restaurant, List<MenuItemOrderDto> menuItems) {
        ProcessOrderRequest processOrderRequest = orderProcessingUtil.buildProcessOrderRequest(user, restaurant, menuItems);
        Optional<OrderDto> newOrder = orderProcessingClientProxy.createOrder(processOrderRequest);
        if (newOrder.isPresent()) {
            SearchOrderDTO searchOrderDTO = createSearchOrderDTO(newOrder.get());
            searchOrdersClientProxy.createOrder(searchOrderDTO);
            rabbitMQSender.send("New order created! Order id: " + searchOrderDTO.getOrderId());
        }
        return newOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private SearchOrderDTO createSearchOrderDTO(OrderDto orderDto) {
        return SearchOrderDTO.builder()
                .userId(orderDto.getUserId())
                .orderId(orderDto.getOrderId())
                .createdAt(orderDto.getCreatedAt())
                .restaurantName(orderDto.getRestaurantName())
                .menuItems(orderDto.getMenuItems().stream().map(MenuItemOrderDto::getName).collect(Collectors.toList()))
                .build();
    }

    @PutMapping("/order/{id}/status")
    public ResponseEntity<OrderStatus> updateOrderStauts(@PathVariable String
                                                                 id, @Valid @RequestBody OrderStatusUpdateRequest orderStatusUpdateRequest) {
        OrderStatus updatedStatus = orderProcessingClientProxy.updateOrderStatus(id, orderStatusUpdateRequest);
        if (updatedStatus != null) {
            rabbitMQSender.send("Order with id " + id + "changed its status to " + orderStatusUpdateRequest.getStatus());
            return ResponseEntity.ok(updatedStatus);
        }
        return ResponseEntity.badRequest().build();
    }

}