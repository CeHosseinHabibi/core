package com.habibi.core.entity;

import com.habibi.core.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name="account-id")
    private Account account;

    private TransactionType transactionType;
    private Long amount;
    private Date timestamp;
    private Boolean isRollbacked = false;
    private Long rollbackFor;
}
