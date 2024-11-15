package com.example.authservice.repository;

import com.example.authservice.model.CertificateRevocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CertificateRevocationRepository extends JpaRepository<CertificateRevocation, BigInteger> {
    Optional<CertificateRevocation> findBySerialNumber(BigInteger serialNumber);
}