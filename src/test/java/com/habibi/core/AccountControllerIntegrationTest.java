package com.habibi.core;

import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.entity.Account;
import com.habibi.core.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerIntegrationTest {

    @LocalServerPort
    private int testServerPort;
    private static String testServerRootUrl;
    @Autowired
    AccountRepository accountRepository;
    private static final int THREADS_COUNT = 3;

    public static String getTestServerRootUrl() {
        return testServerRootUrl;
    }

    @PostConstruct
    public void initializeTestServerRootUrl() {
        testServerRootUrl = "http://localhost:" + testServerPort;
    }

    @Test
    @SneakyThrows
    public void givenAnValidAccount_whenConcurrentWithdraw_thenBalanceShouldBeOk() {
        long withdrawAmount = 10L;
        Account givenAccount = new Account();
        accountRepository.save(givenAccount);
        Long initialAccountBalance = givenAccount.getBalance();

        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        for (int i = 0; i < THREADS_COUNT; i++)
            taskExecutor.submit(new Thread(new CallWithdraw(givenAccount.getAccountId(), withdrawAmount)));
        taskExecutor.shutdown();
        taskExecutor.awaitTermination(2, TimeUnit.MINUTES);

        Assert.assertEquals(Optional.ofNullable(initialAccountBalance - (THREADS_COUNT * withdrawAmount)), Optional.ofNullable(accountRepository.findByAccountId(givenAccount.getAccountId()).get().getBalance()));
    }
}

@AllArgsConstructor
class CallWithdraw implements Runnable {
    Long accountId;
    Long withdrawAmount;

    @Override
    public void run() {
        HttpEntity<WithdrawDto> withdrawDtoRequest = new HttpEntity<>(new WithdrawDto(accountId, withdrawAmount));
        new RestTemplate().postForEntity(AccountControllerIntegrationTest.getTestServerRootUrl() + "/accounts/withdraw", withdrawDtoRequest, WithdrawResponseDto.class);
    }
}