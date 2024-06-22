package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.mapper.AccountMapper;
import com.habibi.core.repository.AccountRepository;
import com.habibi.core.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "lockStrategy", havingValue = "synchronized")
public class SynchronizedAccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger(SynchronizedAccountServiceImpl.class);
    private static final int MINIMUM_SLEEP_SECONDS = 2;
    private static final int MAXIMUM_SLEEP_SECONDS = 7;
    private TransactionalAccountServiceImpl transactionalAccountServiceImpl;
    private TransactionService transactionService;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;

    public synchronized WithdrawResponseDto withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        waitSomeMoments();
        UUID trackingCode = transactionalAccountServiceImpl.withdraw(withdrawDto);

        if (trackingCode != null) {
            return new WithdrawResponseDto(trackingCode);
        } else {
            //throw exception and retry
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

    public boolean isValid(WithdrawDto withdrawDto) {
        return true; //ToDo implement the logic
    }

    public boolean isValid(RollbackWithdrawDto rollbackWithdrawDto) {
        return true; //ToDo implement the logic
    }

    @Transactional
    public void rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto) {
        waitSomeMoments();

        Transaction withdarwTransaction = transactionRepository
                .findByTrackingCode(rollbackWithdrawDto.getTrackingCode()).orElseThrow();

        if (withdarwTransaction.getIsRollbacked())
            return; //throw an exception;

        Account account = withdarwTransaction.getAccount();

        Transaction rollbackWithdrawTransaction = transactionService.createRollbackWithdrawTransaction(account, withdarwTransaction);
        transactionRepository.save(rollbackWithdrawTransaction);

        withdarwTransaction.setIsRollbacked(true);
        transactionRepository.save(withdarwTransaction);

        account.setBalance(account.getBalance() + rollbackWithdrawTransaction.getAmount());
        accountRepository.save(account);
    }

    @SneakyThrows
    public void waitSomeMoments() {
        TimeUnit.SECONDS.sleep(new Random().nextInt(MINIMUM_SLEEP_SECONDS, MAXIMUM_SLEEP_SECONDS));
    }
}