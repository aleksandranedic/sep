package com.example.paypalservice.repository;

import com.example.paypalservice.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Payment findByPaypalPaymentId(String paypalPaymentId);
}