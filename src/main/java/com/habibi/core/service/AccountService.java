package com.habibi.core.service;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.entity.Transaction;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.exceptions.RollbackingTheRollbackedWithdrawException;
import com.habibi.core.exceptions.WithdrawOfRollbackNotFoundException;

import java.util.List;

public interface AccountService {
    Transaction withdraw(WithdrawDto withdrawDto) throws InsufficientFundsException;

    List<AccountDto> getAll();

    Long save();

    default boolean isValid(WithdrawDto withdrawDto) {
        return true; //ToDo implement the logic
    }

    default boolean isValid(RollbackWithdrawDto rollbackWithdrawDto) {
        return true; //ToDo implement the logic
    }

    Transaction rollbackWithdraw(RollbackWithdrawDto rollbackWithdrawDto)
            throws WithdrawOfRollbackNotFoundException, RollbackingTheRollbackedWithdrawException;
}