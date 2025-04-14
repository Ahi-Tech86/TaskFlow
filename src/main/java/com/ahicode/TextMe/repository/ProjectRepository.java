package com.ahicode.TextMe.repository;

import com.ahicode.TextMe.model.entity.ProjectEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @NotNull Optional<ProjectEntity> findById(@NotNull Long id);
}
