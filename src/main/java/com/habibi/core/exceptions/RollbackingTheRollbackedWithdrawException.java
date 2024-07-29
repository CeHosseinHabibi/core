package com.habibi.core.exceptions;

import com.habibi.core.enums.ErrorCode;

public class RollbackingTheRollbackedWithdrawException extends SystemException {
    public RollbackingTheRollbackedWithdrawException() {
        errorCode = ErrorCode.ROLLBACKING_THE_ROLLBACKED_WITHDRAW;
    }
}