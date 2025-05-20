package com.ahicode.TextMe.unit.service.scheduled;

import com.ahicode.TextMe.model.entity.CompletedTaskEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.repository.CompletedTaskRepository;
import com.ahicode.TextMe.repository.TaskRepository;
import com.ahicode.TextMe.service.scheduled.TaskArchiverScheduler;
import com.ahicode.TextMe.service.factory.task.CompletedTaskEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

public class TaskArchiverSchedulerTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CompletedTaskRepository completedTaskRepository;
    @Mock
    private CompletedTaskEntityFactory completedTaskEntityFactory;

    @InjectMocks
    private TaskArchiverScheduler taskArchiverScheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void archiveTasks_successfulArchived() {
        TaskEntity task1 = new TaskEntity();
        TaskEntity task2 = new TaskEntity();
        List<TaskEntity> doneTasks = Arrays.asList(task1, task2);
        List<TaskEntity> overdueTasks = Arrays.asList(task1, task2);

        when(taskRepository.findDoneTasks()).thenReturn(doneTasks);
        when(taskRepository.findOverdueTasks()).thenReturn(overdueTasks);
        when(completedTaskEntityFactory.makeCompletedTaskEntity(any(TaskEntity.class), anyBoolean()))
                .thenReturn(new CompletedTaskEntity());

        taskArchiverScheduler.archiveTasks();

        verify(taskRepository, times(1)).findDoneTasks();
        verify(taskRepository, times(1)).findOverdueTasks();
        verify(completedTaskEntityFactory, times(4)).makeCompletedTaskEntity(any(TaskEntity.class), anyBoolean());
        verify(completedTaskRepository, times(4)).save(any(CompletedTaskEntity.class));
        verify(taskRepository, times(4)).delete(any(TaskEntity.class));
    }

    @Test
    void archiveTasks_nothingForArchive() {
        List<TaskEntity> doneTasks = List.of();
        List<TaskEntity> overdueTasks = List.of();

        when(taskRepository.findDoneTasks()).thenReturn(doneTasks);
        when(taskRepository.findOverdueTasks()).thenReturn(overdueTasks);

        taskArchiverScheduler.archiveTasks();

        verify(taskRepository, times(1)).findDoneTasks();
        verify(taskRepository, times(1)).findOverdueTasks();
    }
}
