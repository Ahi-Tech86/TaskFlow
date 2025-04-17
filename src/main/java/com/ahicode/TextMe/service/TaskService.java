package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.dto.task.TaskCreateRequestDto;
import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.model.dto.task.TaskUpdateRequestDto;

public interface TaskService {
    TaskDto createTask(Long projectId, Long userId, TaskCreateRequestDto requestDto);
    TaskDto updateTask(Long projectId, Long taskId, Long userId, TaskUpdateRequestDto requestDto);
}
