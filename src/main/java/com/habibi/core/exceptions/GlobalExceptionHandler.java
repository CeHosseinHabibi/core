package com.habibi.core.exceptions;

import com.habibi.core.dto.ErrorDto;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private MessageSource messageSource;

    @ExceptionHandler(value = TosanException.class)
    public ResponseEntity<ErrorDto> handleTosanException(TosanException exception) {
        String message = exception.getMessage() == null ?
                messageSource.getMessage(String.valueOf(exception.getErrorCode()), null, Locale.ENGLISH) :
                exception.getMessage();
        return ResponseEntity.badRequest().body(new ErrorDto(exception.getErrorCode(), message, "Core-Microservice",
                exception.getClass().getSimpleName(), null));
    }
}