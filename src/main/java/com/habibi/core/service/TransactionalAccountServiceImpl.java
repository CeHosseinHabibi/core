package com.habibi.core.service;

import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.repository.AccountRepository;
import com.habibi.core.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionalAccountServiceImpl {
    private static final Logger logger = LogManager.getLogger(TransactionalAccountServiceImpl.class);

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private MessageSource messageSource;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        Account account = accountRepository.findByAccountId(withdrawDto.getAccountId()).orElseThrow();//todo handle exception
        logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " read the balance-> " + account.getBalance()
                + ", this-> " + this + "\n");

        if (withdrawDto.getAmount() >= account.getBalance()) {
            throw new InsufficientFundsException(
                    messageSource.getMessage("insufficient.funds.exception.message", null, Locale.ENGLISH));
        }

        Transaction withdrawTransaction = transactionService.createWithdrawTransaction(withdrawDto, account);
        transactionRepository.save(withdrawTransaction);

        account.setBalance(account.getBalance() - withdrawDto.getAmount());
        accountRepository.save(account);
        logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " write the balance-> " + account.getBalance()
                + ", this-> " + this + "\n");

        withdrawTransaction.setTransactionStatus(TransactionStatus.SUCCESS);
        return withdrawTransaction.getTrackingCode();
    }
}