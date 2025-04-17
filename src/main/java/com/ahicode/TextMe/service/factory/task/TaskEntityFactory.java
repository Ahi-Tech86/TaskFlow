package com.ahicode.TextMe.service.factory.task;

import com.ahicode.TextMe.model.dto.task.TaskCreateRequestDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.model.enums.TaskPriority;
import com.ahicode.TextMe.model.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskEntityFactory {

    public TaskEntity makeTaskEntity(ProjectEntity project, Long creatorId, Long assignedId, TaskPriority taskPriority, TaskCreateRequestDto requestDto) {
        return TaskEntity.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .status(TaskStatus.TO_DO)
                .priority(taskPriority)
                .dueDate(requestDto.getDueDate())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .project(project)
                .assignedId(assignedId)
                .creatorId(creatorId)
                .build();
    }
}
