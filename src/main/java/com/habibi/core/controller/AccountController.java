package com.habibi.core.controller;

import com.habibi.core.dto.*;
import com.habibi.core.enums.ErrorCode;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private final ErrorCode NO_ERROR_CODE = null;
    private final String NO_DESCRIPTION = "";
    private AccountService accountService;

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDto> withdraw(@RequestBody WithdrawDto withdrawDto) throws InsufficientFundsException {
        if (!accountService.isValid(withdrawDto))
            return null; //todo throw an exception

        UUID trackingCode;
        try {
            trackingCode = accountService.withdraw(withdrawDto);
        } catch (ObjectOptimisticLockingFailureException objectOptimisticLockingFailureException) {
            trackingCode = accountService.withdraw(withdrawDto);
        }

        return ResponseEntity.ok(new WithdrawResponseDto(trackingCode, NO_ERROR_CODE, NO_DESCRIPTION));
    }

    @PostMapping("/rollback-withdraw")
    public ResponseEntity<RollbackWithdrawResponseDto> rollbackWithdraw(@RequestBody RollbackWithdrawDto rollbackWithdrawDto) {
        if (!accountService.isValid(rollbackWithdrawDto))
            return null; //todo throw an exception

        UUID trackingCode;
        try {
            trackingCode = accountService.rollbackWithdraw(rollbackWithdrawDto);
        } catch (ObjectOptimisticLockingFailureException objectOptimisticLockingFailureException) {
            trackingCode = accountService.rollbackWithdraw(rollbackWithdrawDto);
        }

        return ResponseEntity.ok(new RollbackWithdrawResponseDto(trackingCode, NO_ERROR_CODE, NO_DESCRIPTION));
    }

    @GetMapping
    public List<AccountDto> getAllAccounts() {
        return accountService.getAll();
    }

    @PostMapping
    public ResponseEntity save() {
        return ResponseEntity.status(HttpStatus.CREATED).body("Created id: " + accountService.save());
    }
}