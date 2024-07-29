package com.habibi.core.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibi.core.controller.AccountController;
import com.habibi.core.dto.RollbackWithdrawDto;
import com.habibi.core.dto.RollbackWithdrawResponseDto;
import com.habibi.core.dto.WithdrawDto;
import com.habibi.core.dto.WithdrawResponseDto;
import com.habibi.core.entity.Transaction;
import com.habibi.core.service.AccountService;
import com.habibi.core.util.Utils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {
    @Mock
    AccountService accountService;
    @InjectMocks
    AccountController accountController;

    @SneakyThrows
    @Test
    public void givenWithdrawDto_whenWithdraw_thenResponseStatusAndBodyShouldBeOk() {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        Transaction withdraw = new Transaction();
        WithdrawDto withdrawDto = new WithdrawDto();
        withdrawDto.setAccountId(1L);
        WithdrawResponseDto withdrawResponseDto =
                new WithdrawResponseDto(Utils.getTransactionDto(withdraw));

        when(accountService.isValid(any(WithdrawDto.class))).thenReturn(true);
        when(accountService.withdraw(any(WithdrawDto.class))).thenReturn(withdraw);

        mockMvc.perform(post("/accounts/withdraw").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(withdrawDto))).andExpect(status().isOk())
                .andExpect(content().bytes(new ObjectMapper().writeValueAsBytes(withdrawResponseDto)));
    }

    @SneakyThrows
    @Test
    public void givenRollbackWithdrawDto_whenRollbackWithdraw_thenResponseStatusAndBodyShouldBeOk() {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        Transaction rollbackWithdraw = new Transaction();
        RollbackWithdrawDto rollbackWithdrawDto = new RollbackWithdrawDto();
        RollbackWithdrawResponseDto rollbackWithdrawResponseDto =
                new RollbackWithdrawResponseDto(Utils.getTransactionDto(rollbackWithdraw));

        when(accountService.isValid(any(RollbackWithdrawDto.class))).thenReturn(true);
        when(accountService.rollbackWithdraw(any(RollbackWithdrawDto.class))).thenReturn(rollbackWithdraw);

        mockMvc.perform(post("/accounts/rollback-withdraw").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(rollbackWithdrawDto))).andExpect(status().isOk())
                .andExpect(content().bytes(new ObjectMapper().writeValueAsBytes(rollbackWithdrawResponseDto)));
    }
}