package com.ahicode.TextMe.model.enums;

import com.ahicode.TextMe.exception.AppException;
import org.springframework.http.HttpStatus;

public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH;

    public static TaskPriority fromName(String name) {
        try {
            return TaskPriority.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(String.format("Task priority name: %s does not exist", name), HttpStatus.BAD_REQUEST);
        }
    }
}
