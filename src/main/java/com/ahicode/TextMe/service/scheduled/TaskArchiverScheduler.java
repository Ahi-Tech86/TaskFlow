package com.ahicode.TextMe.service.scheduled;

import com.ahicode.TextMe.model.entity.CompletedTaskEntity;
import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.repository.CompletedTaskRepository;
import com.ahicode.TextMe.repository.TaskRepository;
import com.ahicode.TextMe.service.factory.task.CompletedTaskEntityFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskArchiverScheduler {

    private final TaskRepository taskRepository;
    private final CompletedTaskEntityFactory factory;
    private final CompletedTaskRepository completedTaskRepository;

    @Transactional
    @Scheduled(cron = "0 55 23 * * ?")
    public void archiveTasks() {
        archiveTasksByStatus(false);
        archiveTasksByStatus(true);
    }

    private void archiveTasksByStatus(boolean isDone) {
        List<TaskEntity> tasksList = isDone ? taskRepository.findDoneTasks() : taskRepository.findOverdueTasks();

        if (tasksList.isEmpty()) {
            log.info("{Scheduled} No {} tasks to archive", determineTaskStatus(isDone));

        } else {
            for (TaskEntity task : tasksList) {
                CompletedTaskEntity completedTask = factory.makeCompletedTaskEntity(task, isDone);
                completedTaskRepository.save(completedTask);
                taskRepository.delete(task);
            }

            log.info(
                    "{Scheduled} All {} tasks {} have been moved to the archive",
                    determineTaskStatus(isDone),
                    tasksList.size()
            );
        }
    }

    private String determineTaskStatus(boolean isDone) {
        return isDone ? "done" : "overdue";
    }
}
