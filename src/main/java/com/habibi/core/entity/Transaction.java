package com.habibi.core.entity;

import com.habibi.core.enums.TransactionStatus;
import com.habibi.core.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(generator = "transaction-sequence-generator")
    @GenericGenerator(
            name = "transaction-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "transaction_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private TransactionType transactionType;
    private Long amount;
    private Date timestamp;
    private Boolean isRollbacked = false;
    private Long rollbackFor;
    private UUID trackingCode = UUID.randomUUID();
    private TransactionStatus transactionStatus;
    private String description;
    @Version
    private Long version;
}