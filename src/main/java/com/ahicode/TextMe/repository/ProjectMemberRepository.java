package com.ahicode.TextMe.repository;

import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import jakarta.persistence.Tuple;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, Long> {
    @NotNull Optional<ProjectMemberEntity> findById(@NotNull Long id);

    @Query(
            "SELECT pm FROM ProjectMemberEntity pm " +
            "JOIN pm.user u " +
            "JOIN pm.project p " +
            "WHERE u.nickname = :nickname AND p.id = :projectId"
    )
    Optional<ProjectMemberEntity> findOptionalByNicknameAndProjectId(@Param("nickname") String nickname, @Param("projectId") Long projectId);

    @Query(
            "SELECT pm FROM ProjectMemberEntity pm " +
            "JOIN pm.user u " +
            "WHERE u.nickname = :nickname AND pm.project.id = :projectId"
    )
    Optional<ProjectMemberEntity> findByNicknameAndProjectId(@Param("nickname") String nickname, @Param("projectId") Long projectId);

    @Query(
            value = "SELECT * FROM project_member AS pm " +
                    "WHERE pm.project_id = :projectId",
            nativeQuery = true
    )
    List<ProjectMemberEntity> getAllProjectMembers(@Param("projectId") Long projectId);

    @Query(
            value = "" +
                    "SELECT p.name, p.description, p.create_at, p.start_date " +
                    "FROM project_member AS pm JOIN project AS p " +
                    "ON pm.project_id = p.id " +
                    "WHERE pm.user_id = :userId"
            , nativeQuery = true)
    List<Tuple> getAllProjectsByUserId(@Param("userId") Long userId);

    @Query(
            value = "SELECT * FROM project_member AS pm " +
                    "WHERE pm.user_id = :userId AND pm.project_id = :projectId"
            , nativeQuery = true
    )
    ProjectMemberEntity getProjectMemberEntityByProjectIdAndUserId(
            @Param("userId") Long userId, @Param("projectId") Long projectId
    );

    @Query(
            value = "SELECT * FROM project_member AS pm " +
                    "WHERE pm.nickname = :nickname AND pm.project_id = :projectId",
            nativeQuery = true
    )
    Optional<ProjectMemberEntity> getOptionalProjectMemberEntityByProjectIdAndUserNickname(
            @Param("nickname") String nickname, @Param("projectId") Long projectId
    );

    @Query(
            value = "SELECT * FROM project_member AS pm " +
                    "WHERE pm.user_id = :userId AND pm.project_id = :projectId",
            nativeQuery = true
    )
    Optional<ProjectMemberEntity> getOptionalProjectMemberEntityByProjectIdAndUserId(
            @Param("userId") Long userId, @Param("projectId") Long projectId
    );

    @Query(
            value = "SELECT p.name, p.description, p.create_at, p.start_date " +
                    "FROM project_member AS pm JOIN project AS p " +
                    "ON pm.project_id = p.id " +
                    "WHERE pm.user_id = :userId AND pm.project_id = :projectId"
            , nativeQuery = true
    )
    Tuple getProjectByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("SELECT COUNT(*) FROM ProjectMemberEntity pm WHERE pm.project.id = :projectId")
    Long countMembersInProject(@Param("projectId") Long projectId);


    @Query(value = "SELECT " +
            "u.nickname, " +
            "pm.role, " +
            "pm.joined_at, " +
            "COALESCE(t.current_tasks, 0) AS current_tasks, " +
            "COALESCE(ct.completed_tasks, 0) AS completed_tasks, " +
            "CASE " +
            "   WHEN ct.completed_tasks = 0 THEN 0 " +
            "   ELSE (ct.successful_tasks * 100.0 / ct.completed_tasks) " +
            "END AS completed_tasks_percentage " +
            "FROM " +
            "project_member AS pm " +
            "JOIN " +
            "app_user AS u ON u.id = pm.user_id " +
            "LEFT JOIN ( " +
            "   SELECT " +
            "       assigned_id, " +
            "       COUNT(*) AS current_tasks " +
            "   FROM " +
            "       tasks " +
            "   WHERE " +
            "       project_id = :projectId " +
            "   GROUP BY " +
            "       assigned_id " +
            ") AS t ON t.assigned_id = pm.user_id " +
            "LEFT JOIN ( " +
            "   SELECT " +
            "       assigned_id, " +
            "       COUNT(*) AS completed_tasks, " +
            "       SUM(CASE WHEN completion_status = 'SUCCESSFUL' THEN 1 ELSE 0 END) AS successful_tasks " +
            "   FROM " +
            "       completed_task " +
            "   WHERE " +
            "       project_id = :projectId " +
            "   GROUP BY " +
            "       assigned_id " +
            ") AS ct ON ct.assigned_id = pm.user_id " +
            "WHERE " +
            "pm.project_id = :projectId", nativeQuery = true)
    List<Object[]> findProjectMembersWithTaskStatus(@Param("projectId") Long projectId);
}
