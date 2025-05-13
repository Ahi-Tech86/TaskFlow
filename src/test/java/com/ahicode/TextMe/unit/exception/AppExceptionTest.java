package com.ahicode.TextMe.unit.exception;

import com.ahicode.TextMe.exception.AppException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppExceptionTest {

    @Test
    void shouldThrowAppException() {
        String message = "Test exception message";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LocalDateTime timestamp = LocalDateTime.now();

        AppException exception = new AppException(message, status);

        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getHttpStatus());
        assertNotNull(exception.getTimestamp());

        long timestampDifference = java.time.Duration.between(timestamp, exception.getTimestamp()).toMillis();
        assertTrue(timestampDifference < 1000, "Timestamp difference should be less than 1 second");
    }
}
