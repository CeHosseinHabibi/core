package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.mapper.AccountMapper;
import com.habibi.core.repository.OptimisticAccountRepository;
import com.habibi.core.repository.TransactionRepository;
import com.habibi.core.util.Utils;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "lockStrategy", havingValue = "optimistic")
public class OptimisticAccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger(OptimisticAccountServiceImpl.class);
    private OptimisticAccountRepository optimisticAccountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private final AccountMapper accountMapper;
    private MessageSource messageSource;
    private static int threadCount = 0;

    @Transactional
    public UUID withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        Utils.waitSomeMoments();
        Account account = optimisticAccountRepository.findByAccountId(withdrawDto.getAccountId()).orElseThrow();//todo handle exception
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " read the balance-> " + account.getBalance()
                + ", this-> " + this);
        if (withdrawDto.getAmount() >= account.getBalance())
            throw new InsufficientFundsException(
                    messageSource.getMessage("insufficient.funds.exception.message", null, Locale.ENGLISH));

        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " A" + ", this-> " + this);
        Transaction withdrawTransaction = transactionService.createWithdrawTransaction(withdrawDto, account);
        transactionRepository.save(withdrawTransaction);
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " B" + ", this-> " + this);
        //we can call the core service in a method
        // and in the method, set withdrawTransaction.setTransactionStatus(TransactionStatus.TIMED_OUT_WITH_CORE);
        account.setBalance(account.getBalance() - withdrawDto.getAmount());
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " C" + ", this-> " + this);
        optimisticAccountRepository.save(account);
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " write the balance-> " + account.getBalance()
                + ", this-> " + this + " " + (++threadCount) + "\n");
        withdrawTransaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(withdrawTransaction);
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " D" + ", this-> " + this);
        return withdrawTransaction.getTrackingCode();
    }

    public List<AccountDto> getAll() {
        List<Account> accounts = optimisticAccountRepository.findAll().stream().toList();
        return accountMapper.accountsToAccountDtos(accounts);
    }

    public Long save() {
        Account account = new Account();
        return optimisticAccountRepository.save(account).getAccountId();
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
        optimisticAccountRepository.save(account);
    }
}
