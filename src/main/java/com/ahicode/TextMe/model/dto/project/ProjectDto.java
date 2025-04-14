package com.ahicode.TextMe.model.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private String name;

    private String description;

    @JsonProperty("create_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createAt;

    @JsonProperty("start_date")
    private LocalDate startDate;
}
