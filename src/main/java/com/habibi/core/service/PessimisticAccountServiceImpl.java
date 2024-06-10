package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.mapper.AccountMapper;
import com.habibi.core.repository.PessimisticAccountRepository;
import com.habibi.core.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "lockStrategy", havingValue = "pessimistic")
public class PessimisticAccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger(PessimisticAccountServiceImpl.class);
    private static final int MINIMUM_SLEEP_SECONDS = 2;
    private static final int MAXIMUM_SLEEP_SECONDS = 7;
    private PessimisticAccountRepository pessimisticAccountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private final AccountMapper accountMapper;
    private MessageSource messageSource;
    private static int threadCount = 0;

    @Transactional
    public WithdrawResponseDto withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException {
        logger.info(this.getClass().getCanonicalName());
        waitSomeMoments();
        Account account = pessimisticAccountRepository.findByAccountId(withdrawDto.getAccountId()).orElseThrow();//todo handle exception
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " read the balance-> " + account.getBalance()
                + ", this-> " + this);
        if (withdrawDto.getAmount() >= account.getBalance())
            throw new InsufficientFundsException(
                    messageSource.getMessage("insufficient.funds.exception.message", null, Locale.ENGLISH));

        Transaction withdrawTransaction = transactionService.createWithdrawTransaction(withdrawDto, account);
        transactionRepository.save(withdrawTransaction);

        //we can call the core service in a method
        // and in the method, set withdrawTransaction.setTransactionStatus(TransactionStatus.TIMED_OUT_WITH_CORE);
        account.setBalance(account.getBalance() - withdrawDto.getAmount());
        pessimisticAccountRepository.save(account);
        logger.info("Thread.Id --> " + Thread.currentThread().getId() + " write the balance-> " + account.getBalance()
                + ", this-> " + this + " " + (++threadCount) + "\n");
        withdrawTransaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(withdrawTransaction);
        return new WithdrawResponseDto(withdrawTransaction.getTrackingCode());
    }

    public List<AccountDto> getAll() {
        List<Account> accounts = pessimisticAccountRepository.findAll().stream().toList();
        return accountMapper.accountsToAccountDtos(accounts);
    }

    public Long save() {
        Account account = new Account();
        return pessimisticAccountRepository.save(account).getAccountId();
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
        pessimisticAccountRepository.save(account);
    }

    @SneakyThrows
    public void waitSomeMoments() {
        TimeUnit.SECONDS.sleep(new Random().nextInt(MINIMUM_SLEEP_SECONDS, MAXIMUM_SLEEP_SECONDS));
    }
}
