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
@Document("subscriptions")
public class SubscriptionPlan {
    @Id
    private String id;
    private String name;
    private String plan_id;
    private double amount;

    public SubscriptionPlan(String name, String plan_id, double amount) {
        this.name = name;
        this.plan_id = plan_id;
        this.amount = amount;
    }
}