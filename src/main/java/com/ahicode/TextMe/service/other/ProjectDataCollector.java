package com.ahicode.TextMe.service.other;

import com.ahicode.TextMe.model.dto.report.ReportDto;
import com.ahicode.TextMe.model.dto.report.UserProjectStats;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.enums.CompletionTaskStatus;
import com.ahicode.TextMe.model.enums.ProjectRole;
import com.ahicode.TextMe.model.enums.TaskPriority;
import com.ahicode.TextMe.model.enums.TaskStatus;
import com.ahicode.TextMe.repository.CompletedTaskRepository;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.repository.TaskRepository;
import com.ahicode.TextMe.repository.UserRepository;
import com.ahicode.TextMe.service.ProjectValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectDataCollector {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final CompletedTaskRepository completedTaskRepository;
    private final ProjectValidationService projectValidationService;

    public ReportDto collectProjectData(Long projectId) {
        ProjectEntity project = projectValidationService.isProjectExistsById(projectId);
        String projectOwnerNickname = userRepository.findById(project.getOwnerId()).get().getNickname();

        Map<CompletionTaskStatus, Long> taskCount = new EnumMap<>(CompletionTaskStatus.class);
        List<Object[]> completedTasksDistributionByCompletionStatus = completedTaskRepository.countTasksByCompletionStatus();
        for (Object[] result : completedTasksDistributionByCompletionStatus) {
            CompletionTaskStatus status = (CompletionTaskStatus) result[0];
            Long quantity = (Long) result[1];
            taskCount.put(status, quantity);
        }
        Long successfulTaskCount = taskCount.getOrDefault(CompletionTaskStatus.SUCCESSFUL, 0L);
        if (successfulTaskCount == null) {
            successfulTaskCount = 0L;
        }
        Long unsuccessfulTaskCount = taskCount.getOrDefault(CompletionTaskStatus.UNSUCCESSFUL, 0L);

        Long quantityTasksInProgress = taskRepository.countInProgressTasksByProjectId(projectId);
        Long quantityTasks = taskRepository.countTasksByProjectId(projectId);

        Map<TaskStatus, Long> tasksCountByStatus = new EnumMap<>(TaskStatus.class);
        List<Object[]> currentTasksDistributionByStatus = taskRepository.countTasksByStatus(projectId);
        for (Object[] result : currentTasksDistributionByStatus) {
            TaskStatus status = (TaskStatus) result[0];
            Long quantity = (Long) result[1];
            tasksCountByStatus.put(status, quantity);
        }
        Long todoTaskQuantity = tasksCountByStatus.getOrDefault(TaskStatus.TO_DO, 0L);
        Long inProgressTaskQuantity = tasksCountByStatus.getOrDefault(TaskStatus.IN_PROGRESS, 0L);
        Long needsToApprovalTaskQuantity = tasksCountByStatus.getOrDefault(TaskStatus.NEEDS_APPROVAL, 0L);

        Map<TaskPriority, Long> tasksCountByPriority = new EnumMap<>(TaskPriority.class);
        List<Object[]> currentTasksDistributionByPriority = taskRepository.countTasksByPriority(projectId);
        for (Object[] result : currentTasksDistributionByPriority) {
            TaskPriority priority = (TaskPriority) result[0];
            Long quantity = (Long) result[1];
            tasksCountByPriority.put(priority, quantity);
        }
        Long lowPriorityTasksQuantity = tasksCountByPriority.getOrDefault(TaskPriority.LOW, 0L);
        Long mediumPriorityTasksQuantity = tasksCountByPriority.getOrDefault(TaskPriority.MEDIUM, 0L);
        Long highPriorityTasksQuantity = tasksCountByPriority.getOrDefault(TaskPriority.HIGH, 0L);

        Long quantityOfProjectMembers = projectMemberRepository.countMembersInProject(projectId);

        Map<String, UserProjectStats> projectMembersStatsMap = new HashMap<>();
        List<Object[]> projectMembersStats = projectMemberRepository.findProjectMembersWithTaskStatus(projectId);
        for (Object[] result : projectMembersStats) {
            String nickname = (String) result[0];
            ProjectRole role = ProjectRole.fromName((String) result[1]);
            LocalDateTime joinedAt = ((Timestamp) result[2]).toLocalDateTime();
            Long currentTasks = (Long) result[3];
            Long completedTasks = (Long) result[4];
            BigDecimal completedTasksPercentage = (BigDecimal) result[5];

            UserProjectStats userProjectStats = UserProjectStats.builder()
                    .role(role)
                    .joinedAt(joinedAt)
                    .currentTasks(currentTasks)
                    .completedTasks(completedTasks)
                    .completedTasksPercentage(completedTasksPercentage)
                    .build();

            projectMembersStatsMap.put(nickname, userProjectStats);
        }

        return ReportDto.builder()
                .projectName(project.getName())
                .projectDescription(project.getDescription())
                .projectCreateAt(project.getCreateAt())
                .projectStartDate(project.getStartDate())
                .projectOwnerNickname(projectOwnerNickname)
                .quantityOfProjectMembers(quantityOfProjectMembers)
                .projectMembersStatsMap(projectMembersStatsMap)
                .allCurrentTasks(quantityTasks)
                .currentTasksInTodoStatus(todoTaskQuantity)
                .currentTasksInProgressStatus(inProgressTaskQuantity)
                .currentTasksInNeedsApprovalStatus(needsToApprovalTaskQuantity)
                .currentTasksWithLowPriority(lowPriorityTasksQuantity)
                .currentTasksWithMediumPriority(mediumPriorityTasksQuantity)
                .currentTasksWithHighPriority(highPriorityTasksQuantity)
                .successfulCompletedTasksInProject(successfulTaskCount)
                .unsuccessfulCompletedTasksInProject(unsuccessfulTaskCount)
                .allCompletedTasksInProject(successfulTaskCount + unsuccessfulTaskCount)
                .build();
    }
}
