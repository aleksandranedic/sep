package com.example.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    @Id
    String id;
    String paymentId;
    String successUrl;
    String failedUrl;
    String errorUrl;

    public Response(  String paymentId, String successUrl, String failedUrl, String errorUrl) {
        this.paymentId = paymentId;
        this.successUrl = successUrl;
        this.failedUrl = failedUrl;
        this.errorUrl = errorUrl;
    }
}
