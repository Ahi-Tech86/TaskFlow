package com.ahicode.TextMe.service.factory.project;

import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.enums.ProjectRole;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProjectMemberEntityFactory {
    public ProjectMemberEntity makeProjectMemberEntityForProjectCreator(ProjectEntity entity, ProjectRole role, String userNickname) {
        return ProjectMemberEntity.builder()
                .userId(entity.getOwnerId())
                .memberNickname(userNickname)
                .projectId(entity.getId())
                .role(role)
                .joinedAt(LocalDate.now())
                .build();
    }

    public ProjectMemberEntity makeProjectMemberEntity(Long userId, String nickname, Long projectId, ProjectRole role) {
        return ProjectMemberEntity.builder()
                .userId(userId)
                .memberNickname(nickname)
                .projectId(projectId)
                .role(role)
                .joinedAt(LocalDate.now())
                .build();
    }
}
