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
            value = "SELECT p.name, p.description, p.create_at, p.start_date " +
                    "FROM project_member AS pm JOIN project AS p " +
                    "ON pm.project_id = p.id " +
                    "WHERE pm.user_id = :userId AND pm.project_id = :projectId"
            , nativeQuery = true
    )
    Tuple getProjectByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
