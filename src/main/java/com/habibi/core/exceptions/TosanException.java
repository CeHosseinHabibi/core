package com.habibi.core.exceptions;

import com.habibi.core.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TosanException extends Exception {
    protected ErrorCode errorCode;
    protected String message;
    protected String[] additionalDescription;
}