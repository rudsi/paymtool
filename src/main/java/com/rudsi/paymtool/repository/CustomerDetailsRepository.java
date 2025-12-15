package com.rudsi.paymtool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rudsi.paymtool.entity.CustomerDetails;

public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Long> {
    Optional<CustomerDetails> findByEncryptedCardNumber(String encryptedCardNumber);
}
