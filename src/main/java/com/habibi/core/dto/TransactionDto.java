package com.habibi.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long transactionId;
    private String transactionType;
    private Long amount;
    private Date timestamp;
    private Boolean isRollbacked;
    private Long rollbackFor;
}
