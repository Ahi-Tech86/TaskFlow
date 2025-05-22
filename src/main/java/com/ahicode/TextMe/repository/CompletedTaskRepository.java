package com.ahicode.TextMe.repository;

import com.ahicode.TextMe.model.entity.CompletedTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompletedTaskRepository extends JpaRepository<CompletedTaskEntity, Long> {

    @Query("SELECT c.status AS completionStatus, COUNT(c) AS quantity From CompletedTaskEntity c GROUP BY c.status")
    List<Object[]> countTasksByCompletionStatus();
}
