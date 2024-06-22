package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.exceptions.InsufficientFundsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {
    WithdrawResponseDto withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException;

    List<AccountDto> getAll();

    Long save();

    default boolean isValid(WithdrawDto withdrawDto) {
        return true; //ToDo implement the logic
    }

    default boolean isValid(RollbackWithdrawDto rollbackWithdrawDto) {
        return true; //ToDo implement the logic
    }

    @Transactional
    void rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto);
}
