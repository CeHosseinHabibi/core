package com.habibi.core.repository;

import com.habibi.core.entity.RequesterEntity;
import com.habibi.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByRequesterEntity(RequesterEntity requesterEntity);
}