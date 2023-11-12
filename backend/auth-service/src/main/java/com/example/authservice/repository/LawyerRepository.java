package com.example.authservice.repository;

import com.example.authservice.model.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {
    Optional<Lawyer> findByEmail(String userEmail);
}
