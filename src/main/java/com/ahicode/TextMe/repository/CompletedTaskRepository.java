package com.ahicode.TextMe.repository;

import com.ahicode.TextMe.model.entity.CompletedTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedTaskRepository extends JpaRepository<CompletedTaskEntity, Long> {
}
