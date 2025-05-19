package com.ahicode.TextMe.repository;

import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query(value = "SELECT * FROM tasks WHERE status = 'DONE'", nativeQuery = true)
    List<TaskEntity> findDoneTasks();

    @Query(value = "SELECT * FROM tasks WHERE due_date < CURRENT_DATE", nativeQuery = true)
    List<TaskEntity> findOverdueTasks();

    @Query(value = "SELECT * FROM tasks AS t WHERE t.project_id = :projectId", nativeQuery = true)
    List<TaskEntity> findTasksByProjectId(Long projectId);

    @Query(
            value = "SELECT * FROM tasks AS t " +
                    "WHERE t.id = :taskId AND t.project_id = :projectId",
            nativeQuery = true
    )
    Optional<TaskEntity> getOptionalTaskByIdAndProjectId(@Param("projectId") Long projectId, @Param("taskId") Long taskId);

    @Query(
            "SELECT new com.ahicode.TextMe.model.dto.task.TaskDto(t.title, t.description, t.status, t.priority, t.dueDate, t.createAt, t.updateAt, u1.nickname, u2.nickname) " +
            "FROM TaskEntity t JOIN UserEntity u1 ON t.assignedId = u1.id JOIN UserEntity u2 ON t.creatorId = u2.id " +
            "WHERE t.id = :taskId"
    )
    TaskDto findTaskDtoById(@Param("taskId") Long taskId);

    @Query(
            "SELECT new com.ahicode.TextMe.model.dto.task.TaskDto(t.title, t.description, t.status, t.priority, t.dueDate, t.createAt, t.updateAt, u1.nickname, u2.nickname) " +
                    "FROM TaskEntity t JOIN UserEntity u1 ON t.assignedId = u1.id JOIN UserEntity u2 ON t.creatorId = u2.id " +
                    "WHERE t.project.id = :projectId"
    )
    List<TaskDto> findTaskDtoByProjectId(@Param("projectId") Long projectId);
}
