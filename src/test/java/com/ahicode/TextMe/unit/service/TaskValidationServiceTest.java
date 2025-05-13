package com.ahicode.TextMe.unit.service;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.repository.TaskRepository;
import com.ahicode.TextMe.service.impl.TaskValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskValidationServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskValidationServiceImpl service;

    @Test
    void isTaskExistsById_TaskExist() {
        Long taskId = 1L;
        TaskEntity expectedTask = new TaskEntity();
        expectedTask.setId(taskId);

        when(repository.findById(taskId)).thenReturn(Optional.of(expectedTask));

        TaskEntity actualTask = service.isTaskExistsById(taskId);

        assertThat(actualTask).isEqualTo(expectedTask);
    }

    @Test
    void isTaskExistsById_TaskDoesNotExist() {
        Long taskId = 1L;

        when(repository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.isTaskExistsById(taskId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(String.format("Task with ID %s doesn't exists", taskId))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }

    @Test
    void isTaskBelongsToProject_TaskBelongs() {
        Long projectId = 1L;
        Long taskId = 1L;
        TaskEntity task = new TaskEntity();
        ProjectEntity project = new ProjectEntity();
        task.setId(taskId);
        project.setId(projectId);
        task.setProject(project);

        when(repository.getOptionalTaskByIdAndProjectId(projectId, taskId)).thenReturn(Optional.of(task));

        assertThatCode(() -> service.isTaskBelongsToProject(projectId, taskId)).doesNotThrowAnyException();
    }

    @Test
    void isTaskBelongsToProject_TaskDoesNotBelongs() {
        Long projectId = 1L, taskId = 1L;
        
        when(repository.getOptionalTaskByIdAndProjectId(projectId, taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.isTaskBelongsToProject(projectId, taskId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(String.format("Task with ID %s doesn't belong to project with ID %s", taskId, projectId))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }
}
