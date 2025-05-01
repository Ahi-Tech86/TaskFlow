package com.ahicode.TextMe.service.factory.project;

import com.ahicode.TextMe.model.dto.project.ProjectMemberDto;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMemberDtoFactory {

    public ProjectMemberDto makeProjectMemberDto(ProjectMemberEntity entity) {
        return ProjectMemberDto.builder()
                .nickname(entity.getUser().getNickname())
                .role(entity.getRole())
                .joinedAt(entity.getJoinedAt())
                .build();
    }
}
