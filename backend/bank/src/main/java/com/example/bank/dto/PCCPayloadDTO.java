package com.example.bank.dto;

import com.example.bank.model.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PCCPayloadDTO {
    CardInfo cardInfo;
    double amount;
    String acquirerOrderId;
    String acquirerTimestamp;
}
