package com.example.paypalservice.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDTO {
    double amount;
    String successUrl;
    String failedUrl;
    String errorUrl;
    String merchantTimestamp;
    String merchantOrderId;
}
