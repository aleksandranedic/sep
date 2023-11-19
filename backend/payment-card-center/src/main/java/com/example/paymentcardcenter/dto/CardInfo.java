package com.example.paymentcardcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
    String pan;
    String securityCode;
    String cardHolderName;
    int expiryMonth;
    int expiryYear;
}
