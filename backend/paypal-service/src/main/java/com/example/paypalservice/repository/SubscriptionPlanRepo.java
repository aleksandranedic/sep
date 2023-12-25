package com.example.paypalservice.repository;

import com.example.paypalservice.model.SubscriptionPlan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubscriptionPlanRepo extends MongoRepository<SubscriptionPlan, String> {

    Optional<SubscriptionPlan> findByName(String name);
}