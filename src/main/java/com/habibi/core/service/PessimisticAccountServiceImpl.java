package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.exceptions.RollbackingTheRollbackedWithdrawException;
import com.habibi.core.exceptions.WithdrawOfRollbackNotFoundException;
import com.habibi.core.mapper.AccountMapper;
import com.habibi.core.repository.PessimisticAccountRepository;
import com.habibi.core.repository.PessimisticTransactionRepository;
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

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "lockStrategy", havingValue = "pessimistic")
public class PessimisticAccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger(PessimisticAccountServiceImpl.class);
    private PessimisticAccountRepository pessimisticAccountRepository;
    private PessimisticTransactionRepository pessimisticTransactionRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private final AccountMapper accountMapper;
    private MessageSource messageSource;

    @Transactional
    public Transaction withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        Utils.waitSomeMoments();
        Account account = pessimisticAccountRepository.findByAccountId(withdrawDto.getAccountId()).orElseThrow();//todo handle exception
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " read the balance-> " + account.getBalance() + "\n");
        if (withdrawDto.getAmount() >= account.getBalance())
            throw new InsufficientFundsException(
                    messageSource.getMessage("insufficient.funds.exception.message", null, Locale.ENGLISH));

        Transaction withdrawTransaction = transactionService.createWithdrawTransaction(withdrawDto, account);
        transactionRepository.save(withdrawTransaction);

        //we can call the core service in a method
        // and in the method, set withdrawTransaction.setTransactionStatus(TransactionStatus.TIMED_OUT_WITH_CORE);
        account.setBalance(account.getBalance() - withdrawDto.getAmount());
        pessimisticAccountRepository.save(account);
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " write the balance-> " + account.getBalance() + "\n");
        withdrawTransaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(withdrawTransaction);
        return withdrawTransaction;
    }

    public List<AccountDto> getAll() {
        List<Account> accounts = pessimisticAccountRepository.findAll().stream().toList();
        return accountMapper.accountsToAccountDtos(accounts);
    }

    public Long save() {
        Account account = new Account();
        return pessimisticAccountRepository.save(account).getAccountId();
    }

    @Transactional
    public Transaction rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto)
            throws WithdrawOfRollbackNotFoundException, RollbackingTheRollbackedWithdrawException {
        Utils.waitSomeMoments();

        Transaction withdrawTransaction = pessimisticTransactionRepository
                .findByRequesterEntity(Utils.getRequesterEntity(rollbackWithdrawDto.getRequesterDto()))
                .orElseThrow(() -> new WithdrawOfRollbackNotFoundException(
                        messageSource.getMessage("not.found.exception.message", null, Locale.ENGLISH)));
        logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " read the withdrawTransaction");
        if (withdrawTransaction.getIsRollbacked())
            throw new RollbackingTheRollbackedWithdrawException(
                    messageSource.getMessage("rollbacking.the.rollbacked.withdraw.exception.message", null, Locale.ENGLISH));

        Account account = pessimisticAccountRepository
                .findByAccountId(withdrawTransaction.getAccount().getAccountId()).orElseThrow();
        logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " read the account");
        Transaction rollbackWithdrawTransaction = transactionService.createRollbackWithdrawTransaction(account, withdrawTransaction);
        pessimisticTransactionRepository.save(rollbackWithdrawTransaction);

        withdrawTransaction.setIsRollbacked(true);
        rollbackWithdrawTransaction.setTransactionStatus(TransactionStatus.SUCCESS);
        logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " set rollbackWithdrawTransaction.setIsRollbacked(true)");

        account.setBalance(account.getBalance() + withdrawTransaction.getAmount());
        pessimisticAccountRepository.save(account);
        logger.info("\n\nThread.Id --> " + Thread.currentThread().getId() + " pessimisticAccountRepository.save(account)");

        return rollbackWithdrawTransaction;
    }
}
