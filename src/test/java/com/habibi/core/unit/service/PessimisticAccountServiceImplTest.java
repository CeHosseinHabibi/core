package com.habibi.core.unit.service;

import com.habibi.core.dto.RequesterDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Account;
import com.habibi.core.entity.RequesterEntity;
import com.habibi.core.entity.Transaction;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.exceptions.RollbackingTheRollbackedWithdrawException;
import com.habibi.core.repository.PessimisticAccountRepository;
import com.habibi.core.repository.PessimisticTransactionRepository;
import com.habibi.core.service.PessimisticAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PessimisticAccountServiceImplTest {
    @Mock
    PessimisticAccountRepository pessimisticAccountRepository;
    @Mock
    PessimisticTransactionRepository pessimisticTransactionRepository;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    PessimisticAccountServiceImpl pessimisticAccountService;

    @Test
    public void givenAnAccount_whenWithdrawMoreThanAccountBalance_thenAnExceptionShouldBeThrown() {
        long givenAccountId = 1L;
        String givenNationalCode = "1";
        Account givenAccount = new Account();
        givenAccount.setAccountId(givenAccountId);
        WithdrawDto givenWithdrawDto = new WithdrawDto(givenAccountId, givenAccount.getBalance() + 1,
                new RequesterDto(new Date(), givenNationalCode));

        when(pessimisticAccountRepository.findByAccountId(givenAccountId)).thenReturn(Optional.of(givenAccount));

        assertThrows(InsufficientFundsException.class, () -> {
            pessimisticAccountService.withdraw(givenWithdrawDto);
        });
    }

    @Test
    public void givenARollbackedWithdraw_whenRollback_thenAnExceptionShouldBeThrown() {
        Transaction givenARollbackedWithdraw = new Transaction();
        givenARollbackedWithdraw.setIsRollbacked(true);

        RollbackWithdrawDto givenRollbackWithdrawDto = new RollbackWithdrawDto();
        RequesterDto givenRequesterDto = new RequesterDto();
        givenRollbackWithdrawDto.setRequesterDto(givenRequesterDto);

        when(pessimisticTransactionRepository.findByRequesterEntity(any(RequesterEntity.class)))
                .thenReturn(Optional.of(givenARollbackedWithdraw));

        assertThrows(RollbackingTheRollbackedWithdrawException.class, () -> {
            pessimisticAccountService.rollbackWithdraw(givenRollbackWithdrawDto);
        });
    }
}