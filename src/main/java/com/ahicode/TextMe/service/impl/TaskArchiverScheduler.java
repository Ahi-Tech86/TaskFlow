package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.model.entity.TaskEntity;
import com.ahicode.TextMe.repository.CompletedTaskRepository;
import com.ahicode.TextMe.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskArchiverScheduler {

    private final TaskRepository taskRepository;
    private final CompletedTaskRepository completedTaskRepository;

    @Transactional
    @Scheduled(fixedDelay = 10000)
    public void archiveOverdueTasks() {
        List<TaskEntity> listOfOverdueTasks = taskRepository.findOverdueTasks();

        log.info(Arrays.toString(listOfOverdueTasks.toArray()));
    }

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void archiveDoneTasks() {
        List<TaskEntity> listOfDoneTasks = taskRepository.findDoneTasks();

        log.info(Arrays.toString(listOfDoneTasks.toArray()));
    }
}
