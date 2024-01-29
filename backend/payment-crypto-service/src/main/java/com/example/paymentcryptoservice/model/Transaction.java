package com.example.paymentcryptoservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("payment")
public class Transaction {
    @Id
    private UUID id;
    private String email;
    private String targetAddress;
    private Double amount;
    private TransactionStatus transactionStatus;
}
