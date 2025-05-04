package com.ahicode.TextMe.model.enums;

import com.ahicode.TextMe.exception.AppException;
import org.springframework.http.HttpStatus;

public enum TaskStatus {
    TO_DO,
    IN_PROGRESS,
    NEEDS_APPROVAL,
    DONE,
    OVERDUE;

    public static TaskStatus fromName(String name) {
        try {
            return TaskStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(String.format("Task status name: %s does not exist", name), HttpStatus.BAD_REQUEST);
        }
    }

    public static TaskStatus changeNextStatus(TaskStatus currentStatus) {
        TaskStatus[] values = TaskStatus.values();
        int nextOrdinal = currentStatus.ordinal() + 1;

        if (nextOrdinal >= values.length) {
            throw new AppException(
                    String.format("There is no next status for %s", currentStatus.toString()),
                    HttpStatus.BAD_REQUEST
            );
        }

        return values[nextOrdinal];
    }
}
