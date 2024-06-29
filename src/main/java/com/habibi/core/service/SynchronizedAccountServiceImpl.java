package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.mapper.AccountMapper;
import com.habibi.core.repository.AccountRepository;
import com.habibi.core.repository.TransactionRepository;
import com.habibi.core.util.Utils;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "lockStrategy", havingValue = "synchronized")
public class SynchronizedAccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger(SynchronizedAccountServiceImpl.class);

    private TransactionalAccountServiceImpl transactionalAccountServiceImpl;
    private TransactionService transactionService;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;

    public synchronized UUID withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        Utils.waitSomeMoments();
        return transactionalAccountServiceImpl.withdraw(withdrawDto);
    }

    public List<AccountDto> getAll() {
        List<Account> accounts = accountRepository.findAll().stream().toList();
        return accountMapper.accountsToAccountDtos(accounts);
    }

    public Long save() {
        Account account = new Account();
        return accountRepository.save(account).getAccountId();
    }

    @Transactional
    public void rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto) {
        Utils.waitSomeMoments();

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
}