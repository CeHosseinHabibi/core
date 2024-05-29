package com.habibi.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(generator = "account-sequence-generator")
    @GenericGenerator(
            name = "account-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "account_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @Column(name = "account-id")
    private Long accountId;

    private Long balance = 100_000_000L;

    @OneToMany(mappedBy="account")
    private List<Transaction> transactions;

    @Version
    private Long version;
}