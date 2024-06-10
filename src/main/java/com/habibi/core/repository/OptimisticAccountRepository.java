package com.habibi.core.repository;

import com.habibi.core.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptimisticAccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(Long id);
}