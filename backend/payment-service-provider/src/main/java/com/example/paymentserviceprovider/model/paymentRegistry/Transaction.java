package com.example.paymentserviceprovider.model.paymentRegistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document("transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    String id;
    String merchantOrderId;
    String merchantTimestamp;
    Map<String, Object> additionalInfo;

    public Transaction(String merchantOrderId, String merchantTimestamp, Map<String, Object> data) {
        this.merchantOrderId = merchantOrderId;
        this.merchantTimestamp = merchantTimestamp;
        this.additionalInfo = data;
    }
}
