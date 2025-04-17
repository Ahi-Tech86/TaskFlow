package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.task.TaskCreateRequestDto;
import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.model.dto.task.TaskUpdateRequestDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.model.enums.TaskPriority;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.repository.TaskRepository;
import com.ahicode.TextMe.service.ProjectValidationService;
import com.ahicode.TextMe.service.TaskService;
import com.ahicode.TextMe.service.factory.task.TaskDtoFactory;
import com.ahicode.TextMe.service.factory.task.TaskEntityFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskDtoFactory dtoFactory;
    private final TaskRepository taskRepository;
    private final TaskEntityFactory entityFactory;
    private final PermissionChecker permissionChecker;
    private final ProjectMemberRepository memberRepository;
    private final ProjectValidationService projectValidationService;

    @Override
    public TaskDto createTask(Long projectId, Long userId, TaskCreateRequestDto requestDto) {
        // Check if a project exists by ID
        ProjectEntity project = projectValidationService.isProjectExistsById(projectId);

        // Getting the entity of the task creator
        ProjectMemberEntity taskCreator = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        String assignedNickname = requestDto.getAssignedTo();
        ProjectMemberEntity assignedTo = memberRepository
                .getOptionalProjectMemberEntityByProjectIdAndUserNickname(assignedNickname, projectId)
                .orElseThrow(
                        () -> {
                            log.error("Attempt to assign task to a user {} who is not a member of the project", assignedNickname);
                            return new AppException(
                                    String.format("User %s is not a member of project", assignedNickname),
                                    HttpStatus.BAD_REQUEST
                            );
                        }
                );

        boolean canCreateTask = permissionChecker.hasPermission(taskCreator, "tasks", Action.CREATE_TASK, null);
        if (!canCreateTask) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        Long assignedId = assignedTo.getUserId();
        TaskPriority priority = (requestDto.getPriority() == null) ? TaskPriority.LOW : TaskPriority.fromName(requestDto.getPriority());

        TaskEntity task = entityFactory.makeTaskEntity(project, taskCreator.getUserId(), assignedId, priority, requestDto);
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Saved new task with ID {}", savedTask.getId());

        return dtoFactory.makeTaskDto(savedTask, assignedTo.getMemberNickname(), taskCreator.getMemberNickname());
    }

    @Override
    public TaskDto updateTask(Long projectId, Long taskId, Long userId, TaskUpdateRequestDto requestDto) {
        return null;
    }
}
