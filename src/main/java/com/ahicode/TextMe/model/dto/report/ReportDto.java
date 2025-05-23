package com.ahicode.TextMe.model.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    // PROJECT
    private String projectName;
    private String projectDescription;
    private String projectOwnerNickname;
    private LocalDateTime projectCreateAt;
    private LocalDate projectStartDate;

    // PROJECT MEMBERS
    private Long quantityOfProjectMembers;
    private Map<String, UserProjectStats> projectMembersStatsMap;

    // CURRENT TASKS
    private Long allCurrentTasks;
    private Long currentTasksInTodoStatus;
    private Long currentTasksInProgressStatus;
    private Long currentTasksInNeedsApprovalStatus;
    private Long currentTasksWithLowPriority;
    private Long currentTasksWithMediumPriority;
    private Long currentTasksWithHighPriority;

    // COMPLETED TASKS
    private Long successfulCompletedTasksInProject;
    private Long unsuccessfulCompletedTasksInProject;
    private Long allCompletedTasksInProject;
}
