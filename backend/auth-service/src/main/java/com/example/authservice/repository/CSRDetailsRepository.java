package com.example.authservice.repository;

import com.example.authservice.model.CSRDetails;
import com.example.authservice.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CSRDetailsRepository extends JpaRepository<CSRDetails, UUID> {
    List<CSRDetails> findByStatus(RequestStatus status);
    List<CSRDetails> findAllBySubscriberId(UUID id);
    List<CSRDetails> findAllBySubscriberIdAndStatus(UUID id, RequestStatus status);
}
