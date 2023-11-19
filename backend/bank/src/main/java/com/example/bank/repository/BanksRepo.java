package com.example.bank.repository;

import com.example.bank.model.Banks;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BanksRepo extends MongoRepository<Banks, String> {
    Optional<Banks> findByPan(String pan);
}
