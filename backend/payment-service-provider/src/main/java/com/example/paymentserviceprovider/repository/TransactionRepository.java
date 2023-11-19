package com.example.paymentserviceprovider.repository;

import com.example.paymentserviceprovider.model.paymentRegistry.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Optional<Transaction> findByMerchantOrderId(String merchantOrderId);
}
