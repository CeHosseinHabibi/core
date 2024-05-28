package com.habibi.core.controller;

import com.habibi.core.dto.AccountDto;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WithdrawDto withdrawDto){
        if(!accountService.isValid(withdrawDto))
            return; //todo throw an exception

        accountService.withdraw(withdrawDto);
    }

    @PostMapping("/rollback-withdraw")
    public void rollbackWithdraw(@RequestBody RollbackWithdrawDto rollbackWithdrawDto){
        if(!accountService.isValid(rollbackWithdrawDto))
            return; //todo throw an exception

        accountService.rollbackWithdraw(rollbackWithdrawDto);
    }

    @GetMapping
    public List<AccountDto> getAllAccounts(){
        return accountService.getAll();
    }

    @PostMapping
    public ResponseEntity save(){
        return ResponseEntity.status(HttpStatus.CREATED).body("Created id: " + accountService.save());
    }
}