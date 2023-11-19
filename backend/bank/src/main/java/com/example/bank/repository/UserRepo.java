package com.example.bank.repository;

import com.example.bank.model.CardInfo;
import com.example.bank.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {

    Optional<User> findByCardInfo(CardInfo cardInfo);

}
