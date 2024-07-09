package com.habibi.core.dto;

import com.habibi.core.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RollbackWithdrawResponseDto {
    private TransactionDto transactionDto;
    private ErrorCode errorCode;
    private String description;
}