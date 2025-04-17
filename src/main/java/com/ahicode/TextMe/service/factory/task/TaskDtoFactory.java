package com.ahicode.TextMe.service.factory.task;

import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.model.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity entity, String assignedTo, String creator) {
        return TaskDto.builder()
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .dueDate(entity.getDueDate())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .assignedNickname(assignedTo)
                .creator(creator)
                .build();
    }
}
