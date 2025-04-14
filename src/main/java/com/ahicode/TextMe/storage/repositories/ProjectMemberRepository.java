package com.ahicode.TextMe.storage.repositories;

import com.ahicode.TextMe.storage.entities.ProjectMemberEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, Long> {
    @NotNull Optional<ProjectMemberEntity> findById(@NotNull Long id);
}
