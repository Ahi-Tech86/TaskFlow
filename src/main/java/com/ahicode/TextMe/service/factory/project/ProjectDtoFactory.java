package com.ahicode.TextMe.service.factory.project;

import com.ahicode.TextMe.model.dto.project.ProjectDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.service.factory.DateTimeFactory;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ProjectDtoFactory {

    private final DateTimeFactory dateTimeFactory;

    public ProjectDto makeProjectDto(ProjectEntity entity) {
        return ProjectDto.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .createAt(entity.getCreateAt())
                .build();
    }

    public ProjectDto makeProjectDto(Tuple tuple) {
        return ProjectDto.builder()
                .name(tuple.get("name", String.class))
                .description(tuple.get("description", String.class))
                .createAt(dateTimeFactory.toLocalDateTime(tuple.get("create_at", Instant.class)))
                .startDate(tuple.get("start_date", Date.class).toLocalDate())
                .build();
    }
}
