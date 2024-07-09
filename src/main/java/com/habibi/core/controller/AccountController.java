package com.habibi.core.controller;

import com.habibi.core.dto.*;
import com.habibi.core.entity.Transaction;
import com.habibi.core.enums.ErrorCode;
import com.habibi.core.exceptions.InsufficientFundsException;
import com.habibi.core.exceptions.RollbackingTheRollbackedWithdrawException;
import com.habibi.core.exceptions.WithdrawOfRollbackNotFoundException;
import com.habibi.core.service.AccountService;
import com.habibi.core.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        Transaction withdraw;
        try {
            withdraw = accountService.withdraw(withdrawDto);
        } catch (ObjectOptimisticLockingFailureException objectOptimisticLockingFailureException) {
            withdraw = accountService.withdraw(withdrawDto);
        }

        return ResponseEntity.ok(new WithdrawResponseDto(Utils.getTransactionDto(withdraw), NO_ERROR_CODE, NO_DESCRIPTION));
    }

    @PostMapping("/rollback-withdraw")
    public ResponseEntity<RollbackWithdrawResponseDto> rollbackWithdraw(@RequestBody RollbackWithdrawDto rollbackWithdrawDto)
            throws WithdrawOfRollbackNotFoundException, RollbackingTheRollbackedWithdrawException {
        if (!accountService.isValid(rollbackWithdrawDto))
            return null; //todo throw an exception

        Transaction rollbackWithdraw;
        try {
            rollbackWithdraw = accountService.rollbackWithdraw(rollbackWithdrawDto);
        } catch (ObjectOptimisticLockingFailureException objectOptimisticLockingFailureException) {
            rollbackWithdraw = accountService.rollbackWithdraw(rollbackWithdrawDto);
        }

        return ResponseEntity.ok(new RollbackWithdrawResponseDto(Utils.getTransactionDto(rollbackWithdraw), NO_ERROR_CODE, NO_DESCRIPTION));
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