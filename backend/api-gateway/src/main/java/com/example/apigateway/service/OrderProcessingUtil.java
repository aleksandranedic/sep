package com.example.apigateway.service;

import com.example.shared.model.order.MenuItemOrderDto;
import com.example.shared.model.order.MenuItemProcessOrderRequest;
import com.example.shared.model.order.ProcessOrderRequest;
import com.example.shared.model.restaurant.MenuItemDto;
import com.example.shared.model.restaurant.RestaurantDto;
import com.example.shared.model.user.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderProcessingUtil {

    public ProcessOrderRequest buildProcessOrderRequest(UserDto user, RestaurantDto restaurant, List<MenuItemOrderDto> menuItems) {

        return ProcessOrderRequest.builder()
                .userName(user.getName())
                .userId(user.getId())
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .deliveryRate(restaurant.getDeliveryRate())
                .menuItems(menuItems)
                .build();
    }

    public List<MenuItemOrderDto> convertToOrderMenuItems(List<MenuItemProcessOrderRequest> menuItemProcessOrderRequests, RestaurantDto restaurant) {
        return menuItemProcessOrderRequests.stream()
                .filter(menuItem -> restaurant.getMenuItems().stream().anyMatch(i -> i.getId() == menuItem.getId()))
                .map(menuItem -> {
                    MenuItemDto menuItemDto = restaurant.getMenuItems().stream().filter(i -> i.getId() == menuItem.getId()).findFirst().get();
                    return MenuItemOrderDto.builder()
                            .count(menuItem.getCount())
                            .id(menuItem.getId())
                            .description(menuItemDto.getDescription())
                            .name(menuItemDto.getName())
                            .price(menuItemDto.getPrice())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
