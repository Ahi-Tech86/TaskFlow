package com.ahicode.TextMe.config.security.accessControl;

import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.UserEntity;

@FunctionalInterface
public interface PermissionCheck<T> {
    boolean check(ProjectMemberEntity member, T resource);
}
