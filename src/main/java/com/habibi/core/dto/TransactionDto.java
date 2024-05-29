package com.habibi.core.dto;

import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long transactionId;
    private TransactionType transactionType;
    private Long amount;
    private Date timestamp;
    private Boolean isRollbacked;
    private Long rollbackFor;
    private UUID trackingCode;
    private TransactionStatus transactionStatus;
    private String description;
}