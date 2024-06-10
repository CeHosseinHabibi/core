package com.habibi.core.repository;

import com.habibi.core.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PessimisticAccountRepository extends JpaRepository<Account, Long> {
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findByAccountId(Long id);
}