package com.example.paypalservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document("payment")
public class Payment {
    @Id
    private String id;

    private double amount;

    private boolean isSuccessful;

    private LocalDateTime transactionDate;

    private String merchantOrderId;

    private String paypalPaymentId;
}
