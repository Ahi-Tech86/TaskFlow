package com.ahicode.TextMe.unit.config.accessControl;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.config.security.accessControl.Permissions;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PermissionsTest {

    @InjectMocks
    private PermissionChecker permissionChecker;

    @Mock
    private Permissions permissions;

    private ProjectMemberEntity projectManager;
    private ProjectMemberEntity projectMember;
    private ProjectMemberEntity stakeholder;
    private ProjectMemberEntity teamLead;

    @BeforeEach
    void setup() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("user@email.com")
                .nickname("nickname")
                .firstname("firstname")
                .lastname("lastname")
                .password("1")
                .role(AppRole.USER)
                .createAt(Instant.now())
                .build();

        ProjectEntity project = ProjectEntity.builder()
                .id(1L)
                .name("project")
                .description("description")
                .ownerId(1L)
                .createAt(LocalDateTime.now())
                .build();

        projectManager = ProjectMemberEntity.builder()
                .id(1L)
                .user(user)
                .role(ProjectRole.PROJECT_MANAGER)
                .joinedAt(LocalDate.now())
                .project(project)
                .build();

        projectMember = ProjectMemberEntity.builder()
                .id(1L)
                .user(user)
                .role(ProjectRole.PROJECT_MEMBER)
                .joinedAt(LocalDate.now())
                .project(project)
                .build();

        stakeholder = ProjectMemberEntity.builder()
                .id(1L)
                .user(user)
                .role(ProjectRole.STAKEHOLDER)
                .joinedAt(LocalDate.now())
                .project(project)
                .build();

        teamLead = ProjectMemberEntity.builder()
                .id(1L)
                .user(user)
                .role(ProjectRole.TEAM_LEAD)
                .joinedAt(LocalDate.now())
                .project(project)
                .build();

        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class ProjectManagerPermissionsTest {

        @Nested
        class ProjectResource {

            @Test
            void shouldHavePermissionsForView() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "project", Action.READ_PROJECT, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForUpdate() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "project", Action.UPDATE_PROJECT, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForDelete() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "project", Action.DELETE_PROJECT, null);
                assertTrue(hasPermission);
            }
        }

        @Nested
        class ProjectMemberResource {

            @Test
            void shouldHavePermissionsForInvite() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "members", Action.INVITE_MEMBER, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForExclude() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "members", Action.EXCLUDE_MEMBER, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForChangeMemberRole() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "members", Action.CHANGE_MEMBER_ROLE, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForViewListOfMember() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "members", Action.VIEW_LIST_OF_MEMBERS, null);
                assertTrue(hasPermission);
            }
        }

        @Nested
        class TaskResource {

            @Test
            void shouldHavePermissionsForCreate() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "tasks", Action.CREATE_TASK, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForRead() {
                boolean hasPermission = permissionChecker.hasPermission(projectManager, "tasks", Action.READ_TASK, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForAssignTaskToTeamLeadAndProjectMember() {
                boolean hasPermissionAssignTaskToTeamLead = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.ASSIGN_TASK, teamLead
                );
                assertTrue(hasPermissionAssignTaskToTeamLead);

                boolean hasPermissionAssignTaskToProjectMember = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.ASSIGN_TASK, projectMember
                );
                assertTrue(hasPermissionAssignTaskToProjectMember);
            }

            @Test
            void shouldDontHavePermissionForAssignTaskToProjectManagerAndStakeholder() {
                boolean hasPermissionAssignTaskToProjectManager = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.ASSIGN_TASK, projectManager
                );
                assertFalse(hasPermissionAssignTaskToProjectManager);

                boolean hasPermissionAssignTaskToStakeholder = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.ASSIGN_TASK, stakeholder
                );
                assertFalse(hasPermissionAssignTaskToStakeholder);
            }

            @Test
            void shouldHavePermissionsForUpdate() {
                boolean hasPermission = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.UPDATE_INFO_OF_TASK, null
                );
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForChangeTaskStatus() {
                TaskEntity task = makeTaskEntity();
                task.setStatus(TaskStatus.NEEDS_APPROVAL);

                boolean hasPermission = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.CHANGE_STATUS_OF_TASK, task
                );
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForChangeTaskStatus() {
                TaskEntity task = makeTaskEntity();

                boolean hasPermission = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.CHANGE_STATUS_OF_TASK, task
                );
                assertFalse(hasPermission);
            }

            @Test
            void shouldHavePermissionsForDeleteTask() {
                boolean hasPermission = permissionChecker.hasPermission(
                        projectManager, "tasks", Action.DELETE_TASK, null
                );
                assertTrue(hasPermission);
            }
        }
    }

    @Nested
    class TeamLeadPermissionsTest {

        @Nested
        class ProjectResource {

            @Test
            void shouldHavePermissionsForView() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "project", Action.READ_PROJECT, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForUpdate() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "project", Action.UPDATE_PROJECT, null);
                assertFalse(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForDelete() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "project", Action.DELETE_PROJECT, null);
                assertFalse(hasPermission);
            }
        }

        @Nested
        class ProjectMemberResource {

            @Test
            void shouldHavePermissionsForViewListOfMembers() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "members", Action.VIEW_LIST_OF_MEMBERS, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForExclude() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "members", Action.EXCLUDE_MEMBER, projectMember);
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForExclude() {
                boolean hasPermissionForExcludeProjectManager = permissionChecker.hasPermission(
                        teamLead, "members", Action.EXCLUDE_MEMBER, projectManager
                );
                assertFalse(hasPermissionForExcludeProjectManager);

                boolean hasPermissionForExcludeTeamLead = permissionChecker.hasPermission(
                        teamLead, "members", Action.EXCLUDE_MEMBER, teamLead
                );
                assertFalse(hasPermissionForExcludeTeamLead);

                boolean hasPermissionForExcludeStakeholder = permissionChecker.hasPermission(
                        teamLead, "members", Action.EXCLUDE_MEMBER, stakeholder
                );
                assertFalse(hasPermissionForExcludeStakeholder);
            }
        }

        @Nested
        class TaskResource {

            @Test
            void shouldHavePermissionsForCreate() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "tasks", Action.CREATE_TASK, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForView() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "tasks", Action.READ_TASK, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForAssign() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "tasks", Action.ASSIGN_TASK, projectMember);
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForAssign() {
                boolean hasPermissionAssignTaskToProjectManager = permissionChecker.hasPermission(
                        teamLead, "tasks", Action.ASSIGN_TASK, projectManager
                );
                assertFalse(hasPermissionAssignTaskToProjectManager);

                boolean hasPermissionAssignTaskToTeamLead = permissionChecker.hasPermission(
                        teamLead, "tasks", Action.ASSIGN_TASK, teamLead
                );
                assertFalse(hasPermissionAssignTaskToTeamLead);

                boolean hasPermissionAssignTaskToStakeholder = permissionChecker.hasPermission(
                        teamLead, "tasks", Action.ASSIGN_TASK, stakeholder
                );
                assertFalse(hasPermissionAssignTaskToStakeholder);
            }

            @Test
            void shouldHavePermissionsForUpdate() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "tasks", Action.UPDATE_INFO_OF_TASK, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForChangeTaskStatus() {
                TaskEntity firstTask = makeTaskEntity();
                firstTask.setStatus(TaskStatus.NEEDS_APPROVAL);
                firstTask.setAssignedId(2L);

                TaskEntity secondTask = makeTaskEntity();

                boolean hasPermission = permissionChecker.hasPermission(teamLead, "tasks", Action.CHANGE_STATUS_OF_TASK, firstTask);
                assertTrue(hasPermission);

                boolean hasPermissionForOwnTask = permissionChecker.hasPermission(teamLead, "tasks", Action.CHANGE_STATUS_OF_TASK, secondTask);
                assertTrue(hasPermissionForOwnTask);
            }

            @Test
            void shouldDontHavePermissionsForChangeTaskStatus() {
                TaskEntity task = makeTaskEntity();
                task.setAssignedId(2L);

                boolean hasPermission = permissionChecker.hasPermission(teamLead, "tasks", Action.CHANGE_STATUS_OF_TASK, task);
                assertFalse(hasPermission);
            }

            @Test
            void shouldHavePermissionsForDeleteTask() {
                boolean hasPermission = permissionChecker.hasPermission(teamLead, "tasks", Action.DELETE_TASK, null);
                assertTrue(hasPermission);
            }
        }
    }

    @Nested
    class ProjectMemberPermissionsTest {

        @Nested
        class ProjectResource {

            @Test
            void shouldHavePermissionsForView() {
                boolean hasPermission = permissionChecker.hasPermission(projectMember, "project", Action.READ_PROJECT, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForUpdate() {
                boolean hasPermission = permissionChecker.hasPermission(projectMember, "project", Action.UPDATE_PROJECT, null);
                assertFalse(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForDelete() {
                boolean hasPermission = permissionChecker.hasPermission(projectMember, "project", Action.DELETE_PROJECT, null);
                assertFalse(hasPermission);
            }
        }

        @Nested
        class ProjectMemberResource {

            @Test
            void shouldHavePermissionsForViewListOfMembers() {
                boolean hasPermission = permissionChecker.hasPermission(
                        projectMember, "members", Action.VIEW_LIST_OF_MEMBERS, null
                );
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForInviteAndExcludeMember() {
                boolean hasPermissionForInviteNewMember = permissionChecker.hasPermission(
                        projectMember, "members", Action.INVITE_MEMBER, null
                );
                assertFalse(hasPermissionForInviteNewMember);

                boolean hasPermissionForExcludeMember = permissionChecker.hasPermission(
                        projectMember, "members", Action.EXCLUDE_MEMBER, null
                );
                assertFalse(hasPermissionForExcludeMember);
            }

            @Test
            void shouldDontHavePermissionsForChangeMemberRole() {
                boolean hasPermission = permissionChecker.hasPermission(
                        projectMember, "members", Action.CHANGE_MEMBER_ROLE, null
                );
                assertFalse(hasPermission);
            }
        }

        @Nested
        class TaskResource {

            @Test
            void shouldHavePermissionsForCreateTask() {
                boolean hasPermission = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.CREATE_TASK, null
                );
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForViewTask() {
                boolean hasPermission = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.READ_TASK, null
                );
                assertTrue(hasPermission);
            }

            @Test
            void shouldHavePermissionsForUpdateTaskInfo() {
                TaskEntity firstTask = TaskEntity.builder()
                        .id(1L)
                        .title("title")
                        .description("description")
                        .status(TaskStatus.TO_DO)
                        .priority(TaskPriority.MEDIUM)
                        .dueDate(LocalDate.now())
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .assignedId(1L)
                        .creatorId(2L)
                        .build();

                TaskEntity secondTask = TaskEntity.builder()
                        .id(1L)
                        .title("title")
                        .description("description")
                        .status(TaskStatus.TO_DO)
                        .priority(TaskPriority.MEDIUM)
                        .dueDate(LocalDate.now())
                        .createAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .assignedId(1L)
                        .creatorId(1L)
                        .build();

                boolean hasPermission = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.UPDATE_INFO_OF_TASK, firstTask
                );
                assertTrue(hasPermission);

                boolean hasPermission2 = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.UPDATE_INFO_OF_TASK, secondTask
                );
                assertTrue(hasPermission2);
            }

            @Test
            void shouldHavePermissionsForChangeTaskStatus() {
                TaskEntity firstTask = makeTaskEntity();
                firstTask.setCreatorId(2L);

                TaskEntity secondTask = makeTaskEntity();

                boolean hasPermissionToUpdateTaskWithToDoStatus = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.CHANGE_STATUS_OF_TASK, firstTask
                );
                assertTrue(hasPermissionToUpdateTaskWithToDoStatus);

                boolean hasPermissionToUpdateTaskWithInProgressStatus = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.CHANGE_STATUS_OF_TASK, secondTask
                );
                assertTrue(hasPermissionToUpdateTaskWithInProgressStatus);
            }

            @Test
            void shouldDontHavePermissionsForChangeTaskStatus() {
                TaskEntity firstTask = makeTaskEntity();
                firstTask.setStatus(TaskStatus.NEEDS_APPROVAL);

                TaskEntity secondTask = makeTaskEntity();
                secondTask.setStatus(TaskStatus.DONE);

                TaskEntity thirdTask = makeTaskEntity();
                thirdTask.setStatus(TaskStatus.OVERDUE);

                boolean hasPermissionToChangeTaskStatusWithNeedsApprovalStatus = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.CHANGE_STATUS_OF_TASK, firstTask
                );
                assertFalse(hasPermissionToChangeTaskStatusWithNeedsApprovalStatus);

                boolean hasPermissionToChangeTaskStatusWithDoneStatus = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.CHANGE_STATUS_OF_TASK, secondTask
                );
                assertFalse(hasPermissionToChangeTaskStatusWithDoneStatus);

                boolean hasPermissionToChangeTaskStatusWithOverdueStatus = permissionChecker.hasPermission(
                        projectMember, "tasks", Action.CHANGE_STATUS_OF_TASK, thirdTask
                );
                assertFalse(hasPermissionToChangeTaskStatusWithOverdueStatus);
            }
        }
    }

    @Nested
    class StakeholderPermissionsTest {

        @Nested
        class ProjectResource {

            @Test
            void shouldHavePermissionsForView() {
                boolean hasPermission = permissionChecker.hasPermission(stakeholder, "project", Action.READ_PROJECT, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForDelete() {
                boolean hasPermission = permissionChecker.hasPermission(stakeholder, "project", Action.DELETE_PROJECT, null);
                assertFalse(hasPermission);
            }
        }

        @Nested
        class TaskResource {

            @Test
            void shouldHavePermissionsForView() {
                boolean hasPermission = permissionChecker.hasPermission(stakeholder, "tasks", Action.READ_TASK, null);
                assertTrue(hasPermission);
            }

            @Test
            void shouldDontHavePermissionsForDelete() {
                boolean hasPermission = permissionChecker.hasPermission(stakeholder, "tasks", Action.DELETE_TASK, null);
                assertFalse(hasPermission);
            }
        }
    }

    private TaskEntity makeTaskEntity() {
        return TaskEntity.builder()
                .id(1L)
                .title("title")
                .description("description")
                .status(TaskStatus.TO_DO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .assignedId(1L)
                .creatorId(1L)
                .build();
    }
}
