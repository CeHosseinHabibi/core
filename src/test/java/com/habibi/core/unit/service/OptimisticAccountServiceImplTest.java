package com.habibi.core.unit.service;

import com.habibi.core.dto.RequesterDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.RequesterEntity;
import com.habibi.core.entity.Transaction;
import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.exceptions.RollbackingTheRollbackedWithdrawException;
import com.habibi.core.repository.OptimisticAccountRepository;
import com.habibi.core.repository.OptimisticTransactionRepository;
import com.habibi.core.repository.TransactionRepository;
import com.habibi.core.service.OptimisticAccountServiceImpl;
import com.habibi.core.service.TransactionService;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OptimisticAccountServiceImplTest {
    @Mock
    OptimisticAccountRepository optimisticAccountRepository;
    @Mock
    TransactionService transactionService;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    OptimisticTransactionRepository optimisticTransactionRepository;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    OptimisticAccountServiceImpl optimisticAccountService;

    @SneakyThrows
    @Test
    public void givenAnAccount_whenWithdraw_thenWithdrawStatusShouldBeSuccess() {
        long givenAccountId = 1L;
        long givenWithdrawAmount = 100L;
        String givenUserNationalCode = "1";
        Account givenAccount = new Account();
        givenAccount.setAccountId(givenAccountId);
        Transaction givenWithdrawTransaction = new Transaction();
        WithdrawDto givenWithdrawDto = new WithdrawDto(givenAccountId, givenWithdrawAmount, new RequesterDto(LocalDateTime.now(),
                givenUserNationalCode));

        when(optimisticAccountRepository.findByAccountId(givenAccountId)).thenReturn(Optional.of(givenAccount));
        when(transactionService.createWithdrawTransaction(givenWithdrawDto, givenAccount))
                .thenReturn(givenWithdrawTransaction);
        when(transactionRepository.save(givenWithdrawTransaction)).thenReturn(givenWithdrawTransaction);

        Transaction withdraw = optimisticAccountService.withdraw(givenWithdrawDto);
        Assert.assertEquals(TransactionStatus.SUCCESS, withdraw.getTransactionStatus());
    }

    @Test
    public void givenAnAccount_whenWithdrawMoreThanAccountBalance_thenAnExceptionShouldBeThrown() {
        long givenAccountId = 1L;
        String givenNationalCode = "1";
        Account givenAccount = new Account();
        givenAccount.setAccountId(givenAccountId);
        WithdrawDto givenWithdrawDto = new WithdrawDto(givenAccountId, givenAccount.getBalance() + 1,
                new RequesterDto(LocalDateTime.now(), givenNationalCode));

        when(optimisticAccountRepository.findByAccountId(givenAccountId)).thenReturn(Optional.of(givenAccount));

        assertThrows(InsufficientFundsException.class, () -> {
            optimisticAccountService.withdraw(givenWithdrawDto);
        });
    }

    @Test
    public void givenARollbackedWithdraw_whenRollback_thenAnExceptionShouldBeThrown() {
        Transaction givenARollbackedWithdraw = new Transaction();
        givenARollbackedWithdraw.setIsRollbacked(true);

        RollbackWithdrawDto givenRollbackWithdrawDto = new RollbackWithdrawDto();
        RequesterDto givenRequesterDto = new RequesterDto();
        givenRollbackWithdrawDto.setRequesterDto(givenRequesterDto);

        when(optimisticTransactionRepository.findByRequesterEntity(any(RequesterEntity.class)))
                .thenReturn(Optional.of(givenARollbackedWithdraw));

        assertThrows(RollbackingTheRollbackedWithdrawException.class, () -> {
            optimisticAccountService.rollbackWithdraw(givenRollbackWithdrawDto);
        });
    }
}