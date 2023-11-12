package com.example.authservice.repository;

import com.example.authservice.model.CertificateDetails;
import com.example.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateDetailsRepository extends JpaRepository<CertificateDetails, BigInteger> {
    Optional<CertificateDetails> findByAlias(String alias);

    List<CertificateDetails> findBySubscriber(User user);

    Optional<CertificateDetails> findBySerialNumber(BigInteger serialNumber);

    Optional<CertificateDetails> findBySerialNumberAndSubscriber(BigInteger serialNumber, User subscriber);

    List<CertificateDetails> findByCertificateAuthority(boolean certificateAuthority);

    default List<CertificateDetails> findIssuerCertificates() {
        return findByCertificateAuthority(true);
    }
}