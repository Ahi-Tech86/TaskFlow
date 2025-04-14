package com.ahicode.TextMe.storage.repositories;

import com.ahicode.TextMe.storage.entities.ProjectEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @NotNull Optional<ProjectEntity> findById(@NotNull Long id);
}
