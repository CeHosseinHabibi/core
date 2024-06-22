package com.habibi.core.repository;

import com.habibi.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTrackingCode(UUID trackingCode);
}