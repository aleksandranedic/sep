package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPaymentDTO {
    String pan;
    String securityCode;
    String cardHolderName;
    String paymentId;
    int expiryMonth;
    int expiryYear;
}