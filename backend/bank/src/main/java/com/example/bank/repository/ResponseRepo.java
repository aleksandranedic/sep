package com.example.bank.repository;

import com.example.bank.model.Response;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseRepo extends MongoRepository<Response, String> {
    Optional<Response> findByPaymentId(String paymentId);
}
