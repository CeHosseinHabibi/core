package com.habibi.core.service;

import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.Transaction;
import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.enums.TransactionType;
import com.habibi.core.repository.TransactionRepository;
import com.habibi.core.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {
    private TransactionRepository transactionRepository;

    public Transaction createWithdrawTransaction(WithdrawDto withdrawDto, Account account) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAmount(withdrawDto.getAmount());
        transaction.setCreatedAt(new Date());
        transaction.setTransactionStatus(TransactionStatus.CREATED);
        transaction.setRequesterEntity(Utils.getRequesterEntity(withdrawDto.getRequesterDto()));
        return transaction;
    }

    public Transaction createRollbackWithdrawTransaction(Account account, Transaction withdarwTransaction) {
        Transaction rollbackTransaction = new Transaction();
        rollbackTransaction.setAccount(account);
        rollbackTransaction.setTransactionType(TransactionType.ROLLBACK_FOR_WITHDRAW);
        rollbackTransaction.setCreatedAt(new Date());
        rollbackTransaction.setRollbackFor(withdarwTransaction.getTransactionId());

        return rollbackTransaction;
    }
    public List<Transaction> getAll() {
        return transactionRepository.findAll().stream().toList();
    }
}
