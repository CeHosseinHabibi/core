package com.habibi.core.repository;

import com.habibi.core.entity.RequesterEntity;
import com.habibi.core.entity.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PessimisticTransactionRepository extends JpaRepository<Transaction, Long> {
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByRequesterEntity(RequesterEntity requesterEntity);
}