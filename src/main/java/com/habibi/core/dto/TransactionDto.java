package com.habibi.core.dto;

import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long transactionId;
    private TransactionType transactionType;
    private Long amount;
    private LocalDateTime createdAt;
    private Boolean isRollbacked;
    private Long rollbackFor;
    private TransactionStatus transactionStatus;
    private String description;
    private RequesterDto requesterDto;
}