package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.entity.TaskEntity;

public interface TaskValidationService {
    TaskEntity isTaskExistsById(Long taskId);
    void isTaskBelongsToProject(Long projectId, Long taskId);
}
