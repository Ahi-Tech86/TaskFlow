package com.ahicode.TextMe.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ProjectRequestDto {
    @NotBlank(message = "Project name is mandatory")
    @Size(min = 3, max = 50, message = "Project name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Project description is mandatory")
    @Size(min = 3, max = 500, message = "Project description must be between 3 and 500 characters")
    private String description;

    @JsonProperty("start_date")
    @FutureOrPresent(message = "Start date must be in the future or present")
    private LocalDate startDate;
}
