package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.exceptions.InsufficientFundsException;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {
    WithdrawResponseDto withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException;

    List<AccountDto> getAll();

    Long save();

    boolean isValid(WithdrawDto withdrawDto);

    boolean isValid(RollbackWithdrawDto rollbackWithdrawDto);

    @Transactional
    void rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto);

    @SneakyThrows
    void waitSomeMoments();
}
