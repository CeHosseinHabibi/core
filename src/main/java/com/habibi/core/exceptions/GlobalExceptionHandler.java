package com.habibi.core.exceptions;

import com.habibi.core.dto.RollbackWithdrawResponseDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = InsufficientFundsException.class)
    public ResponseEntity<WithdrawResponseDto> handleInsufficientFundsException(InsufficientFundsException ex) {
        return ResponseEntity.badRequest()
                .body(new WithdrawResponseDto(null, ErrorCode.INSUFFICIENT_FUNDS, ex.getMessage()));
    }

    @ExceptionHandler(value = WithdrawOfRollbackNotFoundException.class)
    public ResponseEntity<RollbackWithdrawResponseDto> handleNotFoundException(WithdrawOfRollbackNotFoundException ex) {
        return ResponseEntity.badRequest()
                .body(new RollbackWithdrawResponseDto(null, ErrorCode.WITHDRAW_OF_ROLLBACK_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(value = RollbackingTheRollbackedWithdrawException.class)
    public ResponseEntity<RollbackWithdrawResponseDto> handleRollbackingTheRollbackedWithdrawException(
            RollbackingTheRollbackedWithdrawException ex) {
        return ResponseEntity.badRequest()
                .body(new RollbackWithdrawResponseDto(null, ErrorCode.ROLLBACKING_THE_ROLLBACKED_WITHDRAW, ex.getMessage()));
    }
}