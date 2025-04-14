package com.ahicode.TextMe.config.security;

import com.ahicode.TextMe.model.dto.ErrorDto;
import com.ahicode.TextMe.exception.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<ErrorDto> exceptionHandling(AppException exception) {
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(
                        ErrorDto.builder()
                                .message(exception.getMessage())
                                .timestamp(exception.getTimestamp())
                                .build()
                );
    }
}
