package com.ahicode.TextMe.model.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequestDto {
    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nullable
    @JsonProperty("start_date")
    @FutureOrPresent(message = "Start date must be in the future or present")
    private LocalDate startDate;
}
