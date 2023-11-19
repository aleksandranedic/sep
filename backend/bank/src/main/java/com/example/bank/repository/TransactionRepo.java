package com.example.bank.repository;

import com.example.bank.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction, String> {
    Optional<Transaction> findByPaymentId(String paymentId);
    Optional<Transaction> findByAcquirerOrderId(String acquirerOrderId);
    Optional<Transaction> findByIssuerOrderId(String issuerOrderId);
}
