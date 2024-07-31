package com.habibi.core.util;

import com.habibi.core.dto.RequesterDto;
import com.habibi.core.dto.TransactionDto;
import com.habibi.core.entity.RequesterEntity;
import com.habibi.core.entity.Transaction;
import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Utils {
    private static final int MINIMUM_SLEEP_SECONDS = 2;
    private static final int MAXIMUM_SLEEP_SECONDS = 7;

    @SneakyThrows
    public static void waitSomeMoments() {
//        TimeUnit.SECONDS.sleep(new Random().nextInt(MINIMUM_SLEEP_SECONDS, MAXIMUM_SLEEP_SECONDS));
    }

    public static RequesterEntity getRequesterEntity(RequesterDto requesterDto) {
        return new RequesterEntity(requesterDto.getRequestedAt(), requesterDto.getUserNationalCode());
    }

    public static RequesterDto getRequesterDto(RequesterEntity requesterEntity) {
        return requesterEntity == null ?
                null : new RequesterDto(requesterEntity.getRequestedAt(), requesterEntity.getUserNationalCode());
    }

    public static TransactionDto getTransactionDto(Transaction transaction) {
        return TransactionDto.builder().transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .isRollbacked(transaction.getIsRollbacked())
                .rollbackFor(transaction.getRollbackFor())
                .transactionStatus(transaction.getTransactionStatus())
                .description(transaction.getDescription())
                .requesterDto(getRequesterDto(transaction.getRequesterEntity()))
                .build();
    }
}