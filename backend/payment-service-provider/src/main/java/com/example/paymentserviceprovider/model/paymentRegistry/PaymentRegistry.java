package com.example.paymentserviceprovider.model.paymentRegistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payment-registry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRegistry {
    @Id
    String id;
    String key;
    String url;
}
