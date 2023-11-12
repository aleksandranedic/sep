package com.example.apigateway.controller;

import com.example.apigateway.service.BackOfficeClientProxy;
import com.example.shared.model.restaurant.*;
import com.example.shared.model.user.NotificationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/back-office")
@RequiredArgsConstructor
public class BackOfficeRestaurantController {

    private final BackOfficeClientProxy backOfficeClientProxy;

    @PostMapping("/restaurant")
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody CreateRestaurantRequest createRestaurantRequest) {
        Optional<RestaurantDto> newRestaurant = backOfficeClientProxy.createRestaurant(createRestaurantRequest);
        return newRestaurant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/notification")
    public ResponseEntity<NotificationDto> createNotification(@Valid @RequestBody String notificationDto) {
        Optional<NotificationDto> notification = backOfficeClientProxy.createNotification(notificationDto);
        System.out.println("Notification - api: " + notification);
        return notification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable Long id) {
        Optional<RestaurantDto> restaurant = backOfficeClientProxy.getRestaurant(id);
        return ResponseEntity.of(restaurant);
    }

    @GetMapping("/restaurants")
    public List<RestaurantDto> getRestaurants() {
        return backOfficeClientProxy.getRestaurants();
    }

    @GetMapping("/notifications")
    public List<NotificationDto> getNotifications() {
        return backOfficeClientProxy.getNotifications();
    }

    @GetMapping("/restaurant/{id}/menu-items")
    public ResponseEntity<List<MenuItemDto>> getMenuItems(@PathVariable Long id) {
        List<MenuItemDto> menuItems = backOfficeClientProxy.getMenuItems(id);
        return ResponseEntity.ok(menuItems);
    }

    @PostMapping("/restaurant/{id}/menu-item")
    public ResponseEntity<MenuItemDto> createMenuItem(@PathVariable("id") Long restaurantId, @Valid @RequestBody CreateMenuItemRequest createMenuItemRequest) {
        Optional<MenuItemDto> newMenuItem = backOfficeClientProxy.createMenuItem(restaurantId, createMenuItemRequest);
        return newMenuItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/restaurant/{id}/remove-menu-item")
    public ResponseEntity<RestaurantDto> removeMenuItem(@PathVariable("id") Long restaurantId, @RequestBody String menuItem) {
        Optional<RestaurantDto> restaurantDto = backOfficeClientProxy.removeMenuItem(restaurantId, menuItem);
        return restaurantDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
