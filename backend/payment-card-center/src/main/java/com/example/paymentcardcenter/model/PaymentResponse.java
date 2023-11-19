package com.example.paymentcardcenter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("paymentresponses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    @Id
    String id;
    String paymentId;
    String successUrl;
    String failedUrl;
    String errorUrl;

    public PaymentResponse(String paymentId, String successUrl, String failedUrl, String errorUrl) {
        this.paymentId = paymentId;
        this.successUrl = successUrl;
        this.failedUrl = failedUrl;
        this.errorUrl = errorUrl;
    }
}
