package com.habibi.core.exceptions;

import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = InsufficientFundsException.class)
    public ResponseEntity<WithdrawResponseDto> handleMyCustomException(InsufficientFundsException ex) {
        return ResponseEntity.badRequest()
                .body(new WithdrawResponseDto(null, ErrorCode.INSUFFICIENT_FUNDS, ex.getMessage()));
    }
}