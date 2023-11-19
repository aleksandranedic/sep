package com.example.paymentcardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentDTO {
    private String cardNumber;
    private String cardName;
    private int expiryMonth;
    private int expiryYear;
    private String ccv;
}
