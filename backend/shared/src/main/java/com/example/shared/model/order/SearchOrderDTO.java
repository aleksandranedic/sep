package com.example.shared.model.order;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchOrderDTO {
    @NotNull
    private String orderId;
    @NotNull
    private Long userId;
    @NotNull
    private Date createdAt;
    @NotNull
    private String restaurantName;
    @NotNull
    private List<String> menuItems;
}
