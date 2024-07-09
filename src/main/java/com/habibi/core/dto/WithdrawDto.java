package com.habibi.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawDto {
    private Long accountId;
    private Long amount;
    private RequesterDto requesterDto;
}