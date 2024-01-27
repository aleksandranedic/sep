package com.example.paymentserviceprovider.logger;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepo extends MongoRepository<Log, String> {
}
