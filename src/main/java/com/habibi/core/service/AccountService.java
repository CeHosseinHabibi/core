package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.exceptions.InsufficientFundsException;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    UUID withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException;

    List<AccountDto> getAll();

    Long save();

    default boolean isValid(WithdrawDto withdrawDto) {
        return true; //ToDo implement the logic
    }

    default boolean isValid(RollbackWithdrawDto rollbackWithdrawDto) {
        return true; //ToDo implement the logic
    }

    UUID rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto);
}