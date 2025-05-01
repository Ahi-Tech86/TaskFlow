package com.ahicode.TextMe.service.factory.project;

import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.model.enums.ProjectRole;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProjectMemberEntityFactory {
    public ProjectMemberEntity makeProjectMemberEntityForProjectCreator(ProjectEntity entity, ProjectRole role, UserEntity user) {
        return ProjectMemberEntity.builder()
                .user(user)
                .role(role)
                .project(entity)
                .joinedAt(LocalDate.now())
                .build();
    }

    public ProjectMemberEntity makeProjectMemberEntity(UserEntity user, ProjectEntity project, ProjectRole role) {
        return ProjectMemberEntity.builder()
                .user(user)
                .project(project)
                .role(role)
                .joinedAt(LocalDate.now())
                .build();
    }
}
