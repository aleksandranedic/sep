package com.example.paypalservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/pera")
@RequiredArgsConstructor
public class AuthController {

//    @PostMapping("/restaurant")
//    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody CreateRestaurantRequest createRestaurantRequest) {
//        Optional<RestaurantDto> newRestaurant = backOfficeClientProxy.createRestaurant(createRestaurantRequest);
//        return newRestaurant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
//    }

    @GetMapping("/user")
    public ResponseEntity<Integer> getRestaurants() {
        Optional<Integer> user = Optional.of(100);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
