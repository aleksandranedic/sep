package com.example.apigateway.controller;

import com.example.shared.model.restaurant.CreateRestaurantRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/created-restaurant")
    public String greeting(CreateRestaurantRequest restaurantRequest) {
        return "NEW RESTAURANT CREATED: " + restaurantRequest.getName();
    }

}
