package com.example.shared.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessOrderRequest {

    @NotNull
    private Long userId;
    private String userName;

    @NotNull
    private Long restaurantId;
    private String restaurantName;

    @NotNull
    private Double deliveryRate;

    @NotEmpty
    private List<MenuItemOrderDto> menuItems;
}
