package com.habibi.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RollbackWithdrawResponseDto {
    private TransactionDto transactionDto;
}