package com.habibi.core.dto;

import com.habibi.core.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
    private ErrorCode errorCode;
    private String message;
    private String thrownBy;
    private String errorType;
    private ErrorDto referTo;
}