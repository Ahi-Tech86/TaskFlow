package com.ahicode.TextMe.service.factory.task;

import com.ahicode.TextMe.model.entity.CompletedTaskEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.model.enums.CompletionTaskStatus;
import org.springframework.stereotype.Component;

@Component
public class CompletedTaskEntityFactory {

    public CompletedTaskEntity makeCompletedTaskEntity(TaskEntity task, boolean isDone) {
        return CompletedTaskEntity.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .status(isDone ? CompletionTaskStatus.SUCCESSFUL : CompletionTaskStatus.UNSUCCESSFUL)
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .completedAt(task.getUpdateAt())
                .createAt(task.getCreateAt())
                .project(task.getProject())
                .assignedId(task.getAssignedId())
                .creatorId(task.getCreatorId())
                .build();
    }
}
