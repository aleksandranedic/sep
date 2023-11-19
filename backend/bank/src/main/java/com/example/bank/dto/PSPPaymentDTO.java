package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PSPPaymentDTO {
    public String merchantId;
    public String merchantPassword;
    public double amount;
    public String merchantOrderId;
    public LocalDateTime merchantTimestamp;
    public String successUrl;
    public String failedUrl;
    public String errorUrl;
}
