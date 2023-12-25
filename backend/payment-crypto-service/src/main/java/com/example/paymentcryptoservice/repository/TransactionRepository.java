package com.example.paymentcryptoservice.repository;

import com.example.paymentcryptoservice.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, UUID> {
}
