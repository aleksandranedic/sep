package com.example.paypalservice.logger;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepo extends MongoRepository<Log, String> {
}
