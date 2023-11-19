package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentResponseDTO {
    String redirectionUrl;
    String merchantOrderId;
    String acquirerOrderId;
    String acquirerTimeStamp;
    String paymentId;

    public CardPaymentResponseDTO(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }
}
