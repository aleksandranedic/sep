package com.example.paymentcryptoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionResponseDTO {
    String address;
    UUID transactionId;
    double bitcoins;
}
