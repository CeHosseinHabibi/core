package com.habibi.core.dto;

import com.habibi.core.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseDto {
    private UUID trackingCode;
    private ErrorCode errorCode;
    private String description;
}