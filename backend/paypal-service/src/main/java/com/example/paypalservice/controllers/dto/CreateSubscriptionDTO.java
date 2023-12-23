package com.example.paypalservice.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateSubscriptionDTO {
    Boolean internet;
    Boolean digital;
    Boolean printed;
    Boolean codification;
    Boolean monthly;
    double amount;
    String successUrl;
    String failedUrl;
    String errorUrl;
    String merchantTimestamp;
    String merchantOrderId;
}
