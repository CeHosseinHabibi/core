package com.habibi.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    @Column(name = "account-id")
    private Long accountId;

    private Long balance = 0L;

    @OneToMany(mappedBy="account")
    private List<Transaction> transactions;
}
