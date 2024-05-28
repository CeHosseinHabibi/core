package com.habibi.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long accountId;
    private Long balance;
    private List<TransactionDto> transactions;
}
