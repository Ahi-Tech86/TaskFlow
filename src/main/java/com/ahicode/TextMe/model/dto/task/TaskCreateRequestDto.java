package com.ahicode.TextMe.model.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TaskCreateRequestDto {

    @NotBlank(message = "Task title is mandatory")
    @Size(min = 2, max = 35)
    private String title;

    @NotBlank(message = "Task description is mandatory")
    @Size(min = 2, max = 500)
    private String description;

    @Nullable
    @Size(max = 10)
    private String priority;

    @NotBlank(message = "Task due date is mandatory")
    @JsonProperty("due_date")
    private LocalDate dueDate;

    @Nullable
    @Size(min = 3, max = 50)
    @JsonProperty("assigned_to")
    private String assignedTo;
}
