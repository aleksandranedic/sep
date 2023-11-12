package com.example.shared.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private String orderId;

    private Long userId;
    private String userName;
    private String restaurantName;
    private Double price;
    private Date createdAt;
    private OrderStatus orderStatus;

    private List<MenuItemOrderDto> menuItems;
}
