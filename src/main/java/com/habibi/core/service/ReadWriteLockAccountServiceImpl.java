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
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "lockStrategy", havingValue = "readWriteLock")
public class ReadWriteLockAccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger(ReadWriteLockAccountServiceImpl.class);

    private TransactionalAccountServiceImpl transactionalAccountServiceImpl;
    private AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    @SneakyThrows
    public Transaction withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        Utils.waitSomeMoments();
        boolean isLockAcquired = lock.writeLock().tryLock(10, TimeUnit.SECONDS);
        if (isLockAcquired) {
            logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " acquired lock " + "\n");
            try {
                return transactionalAccountServiceImpl.withdraw(withdrawDto);
            } finally {
                lock.writeLock().unlock();
                logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " released lock " + "\n");
            }
        } else {
//                throw an exception to retry. because the thread con not acquire lock
            return null; //this line should be removed after throwing mentioned exception
        }
    }

    public List<AccountDto> getAll() {
        List<Account> accounts = accountRepository.findAll().stream().toList();
        return accountMapper.accountsToAccountDtos(accounts);
    }

    public Long save() {
        Account account = new Account();
        return accountRepository.save(account).getAccountId();
    }

    public Transaction rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto)
            throws WithdrawOfRollbackNotFoundException, RollbackingTheRollbackedWithdrawException {
        Utils.waitSomeMoments();
        boolean isLockAcquired = false;
        try {
            isLockAcquired = lock.writeLock().tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
        if (isLockAcquired) {
            logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " acquired lock " + "\n");
            try {
                return transactionalAccountServiceImpl.rollbackWithdraw(rollbackWithdrawDto);
            } finally {
                lock.writeLock().unlock();
                logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " released lock " + "\n");
            }
        } else {
//                throw an exception to retry. because the thread con not acquire lock
            return null; //this line should be removed after throwing mentioned exception
        }
    }
}