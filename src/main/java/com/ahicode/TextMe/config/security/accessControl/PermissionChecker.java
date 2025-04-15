package com.ahicode.TextMe.config.security.accessControl;

import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.model.enums.ProjectRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermissionChecker {

    private final Permissions permissions;

    public <T> boolean hasPermission(ProjectMemberEntity member, String resource, Action action, T data) {
        ProjectRole userProjectRole = member.getRole();
        Map<Action, PermissionCheck<?>> resourcePermissions = Permissions.getRolesPermissions()
                .getOrDefault(userProjectRole, Collections.emptyMap())
                .get(resource);
        if (resourcePermissions == null) return false;

        PermissionCheck<?> permission = resourcePermissions.get(action);
        if (permission == null) return false;

        if (permission instanceof PermissionCheck) {
            @SuppressWarnings("unchecked")
            PermissionCheck<T> permissionCheck = (PermissionCheck<T>) permission;
            if (permissionCheck.check(member, data)) {
                return true;
            }
        }

        return false;
    }
}
