package com.example.paymentcardcenter.repository;

import com.example.paymentcardcenter.model.PCCTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PCCTransactionRepo extends MongoRepository<PCCTransaction, String> {
}
