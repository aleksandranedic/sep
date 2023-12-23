package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodePaymentData {
    private String version;
    private String currency;
    private double amount;
    private String receiverAccount;
    private String receiverName;
    private String payerCity;
    private String paymentCode;
    private String paymentPurpose;
}
