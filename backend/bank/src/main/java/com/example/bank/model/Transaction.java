package com.example.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    String id;
    String userId;
    String paymentId;
    String merchantOrderId;
    LocalDateTime merchantTimeStamp;
    double amount;
    CardInfo issuerCardInfo;
    String acquirerOrderId;
    String acquirerTimestamp;
    String issuerOrderId;
    LocalDateTime issuerTimeStamp;
    String bankId;

    public Transaction(String userId,
                       String paymentId,
                       double amount,
                       String merchantOrderId,
                       LocalDateTime merchantTimeStamp, String bankId) {
        this.userId = userId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.merchantOrderId = merchantOrderId;
        this.merchantTimeStamp = merchantTimeStamp;
        this.bankId = bankId;
    }
}
