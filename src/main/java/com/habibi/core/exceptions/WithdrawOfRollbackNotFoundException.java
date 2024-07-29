package com.habibi.core.exceptions;

import com.habibi.core.enums.ErrorCode;

public class WithdrawOfRollbackNotFoundException extends SystemException {
    public WithdrawOfRollbackNotFoundException() {
        errorCode = ErrorCode.WITHDRAW_OF_ROLLBACK_NOT_FOUND;
    }
}