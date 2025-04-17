package com.ahicode.TextMe.repository;

import com.ahicode.TextMe.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
