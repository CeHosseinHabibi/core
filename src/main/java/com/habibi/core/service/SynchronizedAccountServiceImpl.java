package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.exceptions.RollbackingTheRollbackedWithdrawException;
import com.habibi.core.exceptions.WithdrawOfRollbackNotFoundException;
import com.habibi.core.mapper.AccountMapper;
import com.habibi.core.repository.AccountRepository;
import com.habibi.core.util.Utils;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "lockStrategy", havingValue = "synchronized")
public class SynchronizedAccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger(SynchronizedAccountServiceImpl.class);

    private TransactionalAccountServiceImpl transactionalAccountServiceImpl;
    private AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final Object lock = new Object();

    public Transaction withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        Utils.waitSomeMoments();
        synchronized (lock) {
            return transactionalAccountServiceImpl.withdraw(withdrawDto);
        }
    }

    public List<AccountDto> getAll() {
        List<Account> accounts;
        synchronized (lock) {
            accounts = accountRepository.findAll().stream().toList();
        }
        return accountMapper.accountsToAccountDtos(accounts);
    }

    public Long save() {
        Account account = new Account();
        return accountRepository.save(account).getAccountId();
    }

    public Transaction rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto)
            throws WithdrawOfRollbackNotFoundException, RollbackingTheRollbackedWithdrawException {
        Utils.waitSomeMoments();
        synchronized (lock) {
            return transactionalAccountServiceImpl.rollbackWithdraw(rollbackWithdrawDto);
        }
    }
}