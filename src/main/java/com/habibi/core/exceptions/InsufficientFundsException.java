package com.habibi.core.exceptions;

import com.habibi.core.enums.ErrorCode;

public class InsufficientFundsException extends SystemException {
    public InsufficientFundsException() {
        errorCode = ErrorCode.INSUFFICIENT_FUNDS;
    }
}