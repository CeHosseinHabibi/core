package com.habibi.core.integration;

import com.habibi.core.dto.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
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
    private static final int THREADS_COUNT = 30;

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

        String givenUserNationalCode = "111";
        Date givenBaseTime = new Date();
        List<RequesterDto> givenRequesterDtos = new ArrayList<>();
        for (int i = 0; i < THREADS_COUNT; i++)
            givenRequesterDtos.add(new RequesterDto(Date.from(givenBaseTime.toInstant().plus(i, ChronoUnit.SECONDS)),
                    givenUserNationalCode));

        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        for (int i = 0; i < THREADS_COUNT; i++)
            taskExecutor.submit(new WithdrawTask(givenAccount.getAccountId(), withdrawAmount, givenRequesterDtos.get(i)));
        taskExecutor.shutdown();
        boolean isExecutorTerminatedNormally = taskExecutor.awaitTermination(2, TimeUnit.MINUTES);

        Assert.assertTrue(isExecutorTerminatedNormally);
        Assert.assertEquals(Optional.ofNullable(initialAccountBalance - (THREADS_COUNT * withdrawAmount)),
                Optional.ofNullable(accountRepository.findByAccountId(givenAccount.getAccountId()).get().getBalance()));
    }

    @Test
    @SneakyThrows
    public void givenAnValidAccountAndWithdraw_whenConcurrentRollbackForWithdraw_thenBalanceShouldBeRolledBack() {
        long withdrawAmount = 10L;
        Account givenAccount = new Account();
        accountRepository.save(givenAccount);
        Long initialAccountBalance = givenAccount.getBalance();

        String givenUserNationalCode = "111";
        RequesterDto givenRequesterDto = new RequesterDto(new Date(), givenUserNationalCode);

        WithdrawTask givenWithdrawTask = new WithdrawTask(givenAccount.getAccountId(), withdrawAmount, givenRequesterDto);
        WithdrawResponseDto givenWithdrawResponseDto = givenWithdrawTask.call();

        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        for (int i = 0; i < THREADS_COUNT; i++)
            taskExecutor.submit(new RollbackWithdrawTask(givenWithdrawResponseDto.getTransactionDto().getRequesterDto()));
        taskExecutor.shutdown();
        boolean isExecutorTerminatedNormally = taskExecutor.awaitTermination(2, TimeUnit.MINUTES);

        Assert.assertTrue(isExecutorTerminatedNormally);
        Assert.assertEquals(Optional.ofNullable(initialAccountBalance),
                Optional.ofNullable(accountRepository.findByAccountId(givenAccount.getAccountId()).get().getBalance()));
    }

}

@AllArgsConstructor
class WithdrawTask implements Callable<WithdrawResponseDto> {
    Long accountId;
    Long withdrawAmount;
    RequesterDto requesterDto;

    @Override
    public WithdrawResponseDto call() {
        HttpEntity<WithdrawDto> withdrawRequestDto = new HttpEntity<>(new WithdrawDto(accountId, withdrawAmount, requesterDto));
        ResponseEntity<WithdrawResponseDto> withdrawResponseDto = new RestTemplate()
                .postForEntity(AccountControllerIntegrationTest.getTestServerRootUrl() + "/accounts/withdraw",
                        withdrawRequestDto, WithdrawResponseDto.class);
        return withdrawResponseDto.getBody();
    }
}

@AllArgsConstructor
class RollbackWithdrawTask implements Callable<RollbackWithdrawResponseDto> {
    private RequesterDto requesterDto;

    @Override
    public RollbackWithdrawResponseDto call() {
        HttpEntity<RollbackWithdrawDto> rollbackWithdrawRequestDto = new HttpEntity<>(new RollbackWithdrawDto(requesterDto));
        ResponseEntity<RollbackWithdrawResponseDto> rollbackWithdrawResponseDto = new RestTemplate()
                .postForEntity(AccountControllerIntegrationTest.getTestServerRootUrl() + "/accounts/rollback-withdraw"
                        , rollbackWithdrawRequestDto, RollbackWithdrawResponseDto.class);
        return rollbackWithdrawResponseDto.getBody();
    }
}