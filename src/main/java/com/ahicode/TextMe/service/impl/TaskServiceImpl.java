package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.task.TaskCreateRequestDto;
import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.model.dto.task.TaskUpdateRequestDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.model.enums.TaskPriority;
import com.ahicode.TextMe.model.enums.TaskStatus;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.repository.TaskRepository;
import com.ahicode.TextMe.service.ProjectValidationService;
import com.ahicode.TextMe.service.TaskService;
import com.ahicode.TextMe.service.TaskValidationService;
import com.ahicode.TextMe.service.UserValidationService;
import com.ahicode.TextMe.service.factory.task.TaskDtoFactory;
import com.ahicode.TextMe.service.factory.task.TaskEntityFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskDtoFactory dtoFactory;
    private final TaskRepository taskRepository;
    private final TaskEntityFactory entityFactory;
    private final PermissionChecker permissionChecker;
    private final ProjectMemberRepository memberRepository;
    private final TaskValidationService taskValidationService;
    private final UserValidationService userValidationService;
    private final ProjectValidationService projectValidationService;

    @Override
    @Transactional
    public TaskDto createTask(Long projectId, Long userId, TaskCreateRequestDto requestDto) {
        ProjectEntity project = projectValidationService.isProjectExistsById(projectId);

        ProjectMemberEntity taskCreator = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        String assignedNickname = requestDto.getAssignedTo();
        ProjectMemberEntity assignedTo = memberRepository
                .findOptionalByNicknameAndProjectId(assignedNickname, projectId)
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

        Long assignedId = assignedTo.getUser().getId();
        TaskPriority priority = (requestDto.getPriority() == null) ? TaskPriority.LOW : TaskPriority.fromName(requestDto.getPriority());

        TaskEntity task = entityFactory.makeTaskEntity(project, taskCreator.getUser().getId(), assignedId, priority, requestDto);
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Saved new task with ID {}", savedTask.getId());

        return dtoFactory.makeTaskDto(savedTask, assignedTo.getUser().getNickname(), taskCreator.getUser().getNickname());
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long projectId, Long taskId, Long userId, TaskUpdateRequestDto requestDto) {
        projectValidationService.isProjectExistsById(projectId);

        ProjectMemberEntity member = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        TaskEntity task = taskValidationService.isTaskExistsById(taskId);
        taskValidationService.isTaskBelongsToProject(projectId, taskId);

        if (requestDto.getTitle() != null) {
            task.setTitle(requestDto.getTitle());
        }
        if (requestDto.getDescription() != null) {
            task.setDescription(requestDto.getDescription());
        }
        TaskPriority priority = (requestDto.getPriority() == null) ? null : TaskPriority.fromName(requestDto.getPriority());
        if (priority != null) {
            task.setPriority(priority);
        }
        if (requestDto.getDueDate() != null) {
            task.setDueDate(requestDto.getDueDate());
        }
        task.setUpdateAt(LocalDateTime.now());

        boolean canUpdateTaskInfo = permissionChecker.hasPermission(member, "tasks", Action.UPDATE_INFO_OF_TASK, task);
        if (!canUpdateTaskInfo) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        TaskEntity savedTask = taskRepository.save(task);
        log.info("Task information with ID {} was successfully updated", savedTask.getId());

        String assignedNickname = memberRepository
                .getProjectMemberEntityByProjectIdAndUserId(task.getAssignedId(), projectId).getUser().getNickname();

        String creatorNickname = memberRepository
                .getProjectMemberEntityByProjectIdAndUserId(task.getCreatorId(), projectId).getUser().getNickname();

        return dtoFactory.makeTaskDto(savedTask, assignedNickname, creatorNickname);
    }

    @Override
    public TaskDto getTask(Long projectId, Long taskId, Long userId) {
        projectValidationService.isProjectExistsById(projectId);

        ProjectMemberEntity member = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        TaskEntity task = taskValidationService.isTaskExistsById(taskId);
        taskValidationService.isTaskBelongsToProject(projectId, taskId);

        boolean canReadTask = permissionChecker.hasPermission(member, "tasks", Action.READ_TASK, null);
        if (!canReadTask) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        String assignedNickname = memberRepository
                .getProjectMemberEntityByProjectIdAndUserId(task.getAssignedId(), projectId).getUser().getNickname();
        String creatorNickname = memberRepository
                .getProjectMemberEntityByProjectIdAndUserId(task.getCreatorId(), projectId).getUser().getNickname();

        return dtoFactory.makeTaskDto(task, assignedNickname, creatorNickname);
    }

    @Override
    @Transactional
    public TaskDto changeStatus(Long projectId, Long taskId, Long userId) {
        projectValidationService.isProjectExistsById(projectId);

        ProjectMemberEntity member = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        TaskEntity task = taskValidationService.isTaskExistsById(taskId);
        taskValidationService.isTaskBelongsToProject(projectId, taskId);

        boolean canChangeStatus = permissionChecker.hasPermission(member, "tasks", Action.CHANGE_STATUS_OF_TASK, task);
        if (!canChangeStatus) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        task.setStatus(TaskStatus.changeNextStatus(task.getStatus()));
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Task information with ID {} was successfully updated", savedTask.getId());

        String assignedNickname = memberRepository
                .getProjectMemberEntityByProjectIdAndUserId(task.getAssignedId(), projectId).getUser().getNickname();

        String creatorNickname = memberRepository
                .getProjectMemberEntityByProjectIdAndUserId(task.getCreatorId(), projectId).getUser().getNickname();

        return dtoFactory.makeTaskDto(savedTask, assignedNickname, creatorNickname);
    }

    @Override
    @Transactional
    public void deleteTask(Long projectId, Long taskId, Long userId) {
        projectValidationService.isProjectExistsById(projectId);

        ProjectMemberEntity member = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        TaskEntity task = taskValidationService.isTaskExistsById(taskId);
        taskValidationService.isTaskBelongsToProject(projectId, taskId);

        boolean canDeleteTask = permissionChecker.hasPermission(member, "tasks", Action.DELETE_TASK, task);
        if (!canDeleteTask) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        taskRepository.delete(task);
        log.info("Task with ID {} was successfully deleted", task.getId());
    }

    @Override
    @Transactional
    public TaskDto assignTask(Long projectId, Long taskId, Long userId, String assignedTo) {
        projectValidationService.isProjectExistsById(projectId);

        ProjectMemberEntity member = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        TaskEntity task = taskValidationService.isTaskExistsById(taskId);
        taskValidationService.isTaskBelongsToProject(projectId, taskId);

        UserEntity assignedToUser = userValidationService.isUserExistsByNickname(assignedTo);
        ProjectMemberEntity assignedToMember = memberRepository.getProjectMemberEntityByProjectIdAndUserId(assignedToUser.getId(), projectId);

        boolean canAssignTask = permissionChecker.hasPermission(member, "tasks", Action.ASSIGN_TASK, assignedToMember);
        if (!canAssignTask) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        task.setAssignedId(assignedToUser.getId());
        log.info("Task with ID {} was assigned to user with ID {}", taskId, assignedToUser.getId());

        String creatorNickname = memberRepository
                .getProjectMemberEntityByProjectIdAndUserId(task.getCreatorId(), projectId).getUser().getNickname();

        return dtoFactory.makeTaskDto(task, assignedToUser.getNickname(), creatorNickname);
    }

    @Override
    public List<TaskDto> getAllProjectTasks(Long projectId, Long userId) {
        projectValidationService.isProjectExistsById(projectId);

        ProjectMemberEntity member = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        boolean canViewListOfTasks = permissionChecker.hasPermission(member, "tasks", Action.VIEW_LIST_OF_TASKS, null);
        if (!canViewListOfTasks) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        List<TaskEntity> tasksList = taskRepository.findTasksByProjectId(projectId);

        return tasksList.stream()
                .map(task -> dtoFactory.makeTaskDto(task, String.valueOf(task.getAssignedId()), String.valueOf(task.getCreatorId())))
                .collect(Collectors.toList());
    }
}
