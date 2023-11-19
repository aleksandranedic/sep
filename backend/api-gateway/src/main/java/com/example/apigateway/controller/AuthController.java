package com.example.apigateway.controller;

import com.example.apigateway.service.AuthClientProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/pera")
@RequiredArgsConstructor
public class AuthController {

    private final AuthClientProxy authClientProxy;

//    @PostMapping("/restaurant")
//    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody CreateRestaurantRequest createRestaurantRequest) {
//        Optional<RestaurantDto> newRestaurant = backOfficeClientProxy.createRestaurant(createRestaurantRequest);
//        return newRestaurant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
//    }

    @GetMapping("/user")
    public ResponseEntity<Integer> getRestaurants() {
        Optional<Integer> user = authClientProxy.createUser(5L);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}