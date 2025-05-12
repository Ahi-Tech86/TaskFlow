package com.ahicode.TextMe.repository;

import com.ahicode.TextMe.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query(
            value = "SELECT * FROM tasks AS t " +
                    "WHERE t.id = :taskId AND t.project_id = :projectId",
            nativeQuery = true
    )
    Optional<TaskEntity> getOptionalTaskByIdAndProjectId(@Param("projectId") Long projectId, @Param("taskId") Long taskId);
}
