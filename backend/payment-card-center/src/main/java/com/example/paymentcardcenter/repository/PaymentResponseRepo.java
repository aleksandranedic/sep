package com.example.paymentcardcenter.repository;

import com.example.paymentcardcenter.model.PaymentResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentResponseRepo extends MongoRepository<PaymentResponse, String> {
}
