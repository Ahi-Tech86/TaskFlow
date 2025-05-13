package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.repository.TaskRepository;
import com.ahicode.TextMe.service.TaskValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskValidationServiceImpl implements TaskValidationService {

    private final TaskRepository taskRepository;

    @Override
    public TaskEntity isTaskExistsById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> {
                    log.error("Attempt to update non-existent task information with ID {}", taskId);
                    return new AppException(String.format("Task with ID %s doesn't exists"), HttpStatus.BAD_REQUEST);
                }
        );
    }

    @Override
    public void isTaskBelongsToProject(Long projectId, Long taskId) {
        Optional<TaskEntity> optionalTask = taskRepository.getOptionalTaskByIdAndProjectId(projectId, taskId);

        if (optionalTask.isEmpty()) {
            log.error("Attempt to take action on a task that doesn't belong to the project {}", projectId);
            throw new AppException(
                    String.format("Task with ID %s doesn't belong to project with ID %s", taskId, projectId),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
