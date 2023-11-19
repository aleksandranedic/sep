package com.example.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    String id;
    String password;
    double amount;
    CardInfo cardInfo;
    String bankId;
    String name;
    public User(String pass, double amount, CardInfo cardInfo, String bankId, String name) {
        this.password = pass;
        this.amount = amount;
        this.cardInfo = cardInfo;
        this.bankId = bankId;
        this.name = name;
    }

    public User(String pass, double amount, String bankId, String name) {
        this.password = pass;
        this.amount = amount;
        this.bankId = bankId;
        this.name = name;
    }
}
