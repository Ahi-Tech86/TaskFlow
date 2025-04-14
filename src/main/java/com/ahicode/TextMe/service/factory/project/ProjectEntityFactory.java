package com.ahicode.TextMe.service.factory.project;

import com.ahicode.TextMe.model.dto.project.ProjectCreateRequestDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ProjectEntityFactory {
    public ProjectEntity makeProjectEntity(ProjectCreateRequestDto requestDto, Long userId) {
        return ProjectEntity.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .startDate(requestDto.getStartDate())
                .ownerId(userId)
                .createAt(LocalDateTime.now())
                .build();
    }
}
