package com.habibi.core.repository;

import com.habibi.core.entity.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface OptimisticTransactionRepository extends JpaRepository<Transaction, Long> {
    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Transaction> findByTrackingCode(UUID trackingCode);
}