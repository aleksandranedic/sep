package com.example.shared.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemOrderDto {
    private long id;
    private int count;
    private Double price;
    private String name;
    private String description;
}
