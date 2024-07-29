package com.habibi.core.exceptions;

import com.habibi.core.enums.ErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CoreInvocationException extends InvocationException {
    public CoreInvocationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CoreInvocationException(ErrorCode errorCode, String message, String... additionalDescription) {
        this.errorCode = errorCode;
        this.message = message;
        this.additionalDescription = additionalDescription;
    }
}