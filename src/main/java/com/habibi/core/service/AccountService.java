package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.mapper.AccountMapper;
import com.habibi.core.repository.AccountRepository;
import com.habibi.core.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class AccountService {
    private static final int MINIMUM_SLEEP_SECONDS = 2;
    private static final int MAXIMUM_SLEEP_SECONDS = 7;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private final AccountMapper accountMapper;


    @Transactional
    public void withdraw(WithdrawDto withdrawDto){
        waitSomeMoments();

        Account account = accountRepository.findById(withdrawDto.getAccountId()).orElseThrow();//todo handle exception

        Transaction withdrawTransaction = transactionService.createWithdrawTransaction(withdrawDto, account);
        transactionRepository.save(withdrawTransaction);

        account.setBalance(account.getBalance() - withdrawDto.getAmount());
        accountRepository.save(account);
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

        Transaction withdarwTransaction = transactionRepository.findById(rollbackWithdrawDto.getTransactionId()).orElseThrow();
        if(withdarwTransaction.getIsRollbacked())
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
    public void waitSomeMoments(){
        TimeUnit.SECONDS.sleep(new Random().nextInt(MINIMUM_SLEEP_SECONDS, MAXIMUM_SLEEP_SECONDS));
    }
}
