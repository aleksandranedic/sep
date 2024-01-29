package com.example.authservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    PaymentType paymentType;
    UUID userId;

    public Subscription(UUID userId, PaymentType paymentType) {
        this.userId = userId;
        this.paymentType = paymentType;
    }
}
