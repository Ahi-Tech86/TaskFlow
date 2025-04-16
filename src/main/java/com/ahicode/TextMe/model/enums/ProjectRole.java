package com.ahicode.TextMe.model.enums;

import com.ahicode.TextMe.exception.AppException;
import org.springframework.http.HttpStatus;

public enum ProjectRole {
    PROJECT_MANAGER,
    TEAM_LEAD,
    PROJECT_MEMBER,
    STAKEHOLDER;

    public static ProjectRole fromName(String name) {
        try {
            return ProjectRole.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(String.format("Role %s does not exist", name), HttpStatus.BAD_REQUEST);
        }
    }
}
