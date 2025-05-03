package com.ahicode.TextMe.config;

import com.ahicode.TextMe.config.security.RestExceptionHandler;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.errors.ErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestExceptionHandlerTest {

    @InjectMocks
    private RestExceptionHandler restExceptionHandler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldHandleAppException() {
        String errorMessage = "Test error message";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        AppException exception = new AppException(errorMessage, httpStatus);

        ResponseEntity<ErrorDto> response = restExceptionHandler.exceptionHandling(exception);

        ErrorDto errorDto = response.getBody();
        assertEquals(errorMessage, errorDto.getMessage());
        assertEquals(httpStatus, response.getStatusCode());
        assertEquals(exception.getTimestamp(), errorDto.getTimestamp());
    }
}
