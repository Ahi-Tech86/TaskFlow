package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.dto.task.TaskCreateRequestDto;
import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.model.dto.task.TaskUpdateRequestDto;

import java.util.List;

public interface TaskService {
    void deleteTask(Long projectId, Long taskId, Long userId);
    TaskDto getTask(Long projectId, Long taskId, Long userId);
    List<TaskDto> getAllProjectTasks(Long projectId, Long userId);
    TaskDto changeStatus(Long projectId, Long taskId, Long userId);
    TaskDto assignTask(Long projectId, Long taskId, Long userId, String assignedTo);
    TaskDto createTask(Long projectId, Long userId, TaskCreateRequestDto requestDto);
    TaskDto updateTask(Long projectId, Long taskId, Long userId, TaskUpdateRequestDto requestDto);
}
