package com.habibi.core.unit.service;

import com.habibi.core.dto.RequesterDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.RequesterEntity;
import com.habibi.core.entity.Transaction;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.exceptions.RollbackingTheRollbackedWithdrawException;
import com.habibi.core.repository.AccountRepository;
import com.habibi.core.repository.TransactionRepository;
import com.habibi.core.service.TransactionalAccountServiceImpl;
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
public class TransactionalAccountServiceImplTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    TransactionalAccountServiceImpl transactionalAccountService;

    @Test
    public void givenAnAccount_whenWithdrawMoreThanAccountBalance_thenAnExceptionShouldBeThrown() {
        long givenAccountId = 1L;
        String givenNationalCode = "1";
        Account givenAccount = new Account();
        givenAccount.setAccountId(givenAccountId);
        WithdrawDto givenWithdrawDto = new WithdrawDto(givenAccountId, givenAccount.getBalance() + 1,
                new RequesterDto(LocalDateTime.now(), givenNationalCode));

        when(accountRepository.findByAccountId(givenAccountId)).thenReturn(Optional.of(givenAccount));

        assertThrows(InsufficientFundsException.class, () -> {
            transactionalAccountService.withdraw(givenWithdrawDto);
        });
    }

    @Test
    public void givenARollbackedWithdraw_whenRollback_thenAnExceptionShouldBeThrown() {
        Transaction givenARollbackedWithdraw = new Transaction();
        givenARollbackedWithdraw.setIsRollbacked(true);

        RollbackWithdrawDto givenRollbackWithdrawDto = new RollbackWithdrawDto();
        RequesterDto givenRequesterDto = new RequesterDto();
        givenRollbackWithdrawDto.setRequesterDto(givenRequesterDto);

        when(transactionRepository.findByRequesterEntity(any(RequesterEntity.class)))
                .thenReturn(Optional.of(givenARollbackedWithdraw));

        assertThrows(RollbackingTheRollbackedWithdrawException.class, () -> {
            transactionalAccountService.rollbackWithdraw(givenRollbackWithdrawDto);
        });
    }
}