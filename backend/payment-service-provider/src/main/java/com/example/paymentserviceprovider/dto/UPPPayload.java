package com.example.paymentserviceprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UPPPayload {
    double amount;
    String governmentId;
}
