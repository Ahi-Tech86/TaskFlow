package com.ahicode.TextMe.config.security.accessControl;

import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.model.enums.ProjectRole;
import com.ahicode.TextMe.model.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Permissions {
    private static final Map<ProjectRole, Map<String, Map<Action, PermissionCheck<?>>>> ROLES_PERMISSIONS;

    static {
        ROLES_PERMISSIONS = new HashMap<>();

        // Project Manager permissions
        {
            Map<String, Map<Action, PermissionCheck<?>>> managerPermissions = new HashMap<>();
            managerPermissions.put("project", Map.of(
                    Action.READ_PROJECT, (user, resource) -> true,
                    Action.UPDATE_PROJECT, (user, resource) -> true,
                    Action.DELETE_PROJECT, (user, resource) -> true
            ));
            managerPermissions.put("members", Map.of(
                    Action.INVITE_MEMBER, (user, resource) -> true,
                    Action.EXCLUDE_MEMBER, (user, resource) -> true,
                    Action.CHANGE_MEMBER_ROLE, (user, resource) -> true,
                    Action.VIEW_LIST_OF_MEMBERS, (user, resource) -> true
            ));
            managerPermissions.put("tasks", Map.of(
                    Action.CREATE_TASK, (user, task) -> true,
                    Action.READ_TASK, (user, task) -> true,
                    Action.ASSIGN_TASK, (PermissionCheck<ProjectMemberEntity>) (user, member) -> !member.getRole().equals(ProjectRole.STAKEHOLDER) && !member.getRole().equals(ProjectRole.PROJECT_MANAGER),
                    Action.UPDATE_INFO_OF_TASK, (user, task) -> true,
                    Action.CHANGE_STATUS_OF_TASK, (PermissionCheck<TaskEntity>) (user, task) -> task.getStatus().equals(TaskStatus.NEEDS_APPROVAL),
                    Action.DELETE_TASK, (user, task) -> true
            ));
            ROLES_PERMISSIONS.put(ProjectRole.PROJECT_MANAGER, managerPermissions);
        }

        // Project Team lead permissions
        {
            Map<String, Map<Action, PermissionCheck<?>>> teamLeadPermissions = new HashMap<>();
            teamLeadPermissions.put("project", Map.of(
                    Action.READ_PROJECT, (user, resource) -> true
            ));
            teamLeadPermissions.put("members", Map.of(
                    Action.EXCLUDE_MEMBER, (PermissionCheck<ProjectMemberEntity>) (user, member) -> member.getRole().equals(ProjectRole.PROJECT_MEMBER),
                    Action.VIEW_LIST_OF_MEMBERS, (user, resource) -> true
            ));
            teamLeadPermissions.put("tasks", Map.of(
                    Action.CREATE_TASK, (user, task) -> true,
                    Action.READ_TASK, (user, task) -> true,
                    Action.ASSIGN_TASK, (PermissionCheck<ProjectMemberEntity>) (user, member) -> member.getRole().equals(ProjectRole.PROJECT_MEMBER),
                    Action.UPDATE_INFO_OF_TASK, (user, task) -> true,
                    Action.CHANGE_STATUS_OF_TASK, (PermissionCheck<TaskEntity>) (member, task) -> task.getStatus().equals(TaskStatus.NEEDS_APPROVAL) || task.getAssignedId().equals(member.getUser().getId()),
                    Action.DELETE_TASK, (user, task) -> true
            ));
            ROLES_PERMISSIONS.put(ProjectRole.TEAM_LEAD, teamLeadPermissions);
        }

        // Project member permissions
        {
            Map<String, Map<Action, PermissionCheck<?>>> memberPermissions = new HashMap<>();
            memberPermissions.put("project", Map.of(
                    Action.READ_PROJECT, (user, resource) -> true
            ));
            memberPermissions.put("members", Map.of(
                    Action.VIEW_LIST_OF_MEMBERS, (user, resource) -> true
            ));
            memberPermissions.put("tasks", Map.of(
                    Action.CREATE_TASK, (user, task) -> true,
                    Action.READ_TASK, (user, task) -> true,
                    Action.UPDATE_INFO_OF_TASK, (PermissionCheck<TaskEntity>) (member, task) -> task.getAssignedId().equals(member.getUser().getId()) || task.getCreatorId().equals(member.getUser().getId()),
                    Action.CHANGE_STATUS_OF_TASK, (PermissionCheck<TaskEntity>) (user, task) -> task.getStatus().equals(TaskStatus.TO_DO) || task.getStatus().equals(TaskStatus.IN_PROGRESS)
            ));
            ROLES_PERMISSIONS.put(ProjectRole.PROJECT_MEMBER, memberPermissions);
        }

        // Project stakeholder permissions
        {
            Map<String, Map<Action, PermissionCheck<?>>> stakeholderPermissions = new HashMap<>();
            stakeholderPermissions.put("project", Map.of(
                    Action.READ_PROJECT, (user, resource) -> true
            ));
            stakeholderPermissions.put("tasks", Map.of(
                    Action.READ_TASK, (user, task) -> true
            ));
            ROLES_PERMISSIONS.put(ProjectRole.STAKEHOLDER, stakeholderPermissions);
        }
    }

    public static Map<ProjectRole, Map<String, Map<Action, PermissionCheck<?>>>> getRolesPermissions() {
        return ROLES_PERMISSIONS;
    }
}
