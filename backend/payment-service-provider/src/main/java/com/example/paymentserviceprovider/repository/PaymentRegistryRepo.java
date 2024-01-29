package com.example.paymentserviceprovider.repository;

import com.example.paymentserviceprovider.model.paymentRegistry.PaymentRegistry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRegistryRepo extends MongoRepository<PaymentRegistry, String> {
}
