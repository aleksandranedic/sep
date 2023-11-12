package com.example.shared.model.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {

    private Long id;

    private String name;
    private String address;
    private String phone;
    private String email;

    private Double deliveryRate;
    private List<MenuItemDto> menuItems;
}
