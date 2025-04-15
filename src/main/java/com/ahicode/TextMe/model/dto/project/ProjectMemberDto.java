package com.ahicode.TextMe.model.dto.project;

import com.ahicode.TextMe.model.enums.ProjectRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDto {
    private String nickname;
    private ProjectRole role;
    @JsonProperty("joined_at")
    private LocalDate joinedAt;
}
