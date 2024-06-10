package com.habibi.core;

import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.entity.Account;
import com.habibi.core.repository.AccountRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class AccountControllerIntegrationTest {
    public static final int THREADS_COUNT = 20;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @SneakyThrows
    public void givenAnValidAccount_whenConcurrentWithdraw_thenBalanceShouldBeOk() {
        long withdrawAmount = 10L;
        Account account = new Account();
        accountRepository.save(account);
        Long initialAccountBalance = account.getBalance();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREADS_COUNT; i++) {
            threads.add(new Thread(new CallWithdraw(account.getAccountId(), withdrawAmount)));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        Assert.assertEquals(Optional.ofNullable(initialAccountBalance - (THREADS_COUNT * withdrawAmount)), Optional.ofNullable(accountRepository.findByAccountId(account.getAccountId()).get().getBalance()));

    }
}

class CallWithdraw implements Runnable {
    Long accountId;

    Long withdrawAmount;
    RestTemplate restTemplate = new RestTemplate();

    public CallWithdraw(Long accountId, Long withdrawAmount) {
        this.accountId = accountId;
        this.withdrawAmount = withdrawAmount;
    }

    @Override
    public void run() {
        HttpEntity<WithdrawDto> withdrawDtoRequest = new HttpEntity<>(new WithdrawDto(accountId, withdrawAmount));
        restTemplate.postForEntity("http://localhost:8081/accounts/withdraw", withdrawDtoRequest, WithdrawResponseDto.class);
    }
}