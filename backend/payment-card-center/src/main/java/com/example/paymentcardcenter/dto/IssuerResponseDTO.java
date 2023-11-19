package com.example.paymentcardcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuerResponseDTO {
    String acquirerOrderId;
    String acquirerTimestamp;
    String issuerOrderId;
    String issuerTimestamp;
    double amount;
}
