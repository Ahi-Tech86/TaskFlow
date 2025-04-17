package com.ahicode.TextMe.model.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
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
public class TaskUpdateRequestDto {

    @Nullable
    @Size(min = 2, max = 35)
    private String title;

    @Nullable
    @Size(min = 2, max = 500)
    private String description;

    @Nullable
    @Size(min = 4, max = 15)
    private String status;

    @Nullable
    @Size(min = 3, max = 10)
    private String priority;

    @Nullable
    @JsonProperty("due_date")
    private LocalDate dueDate;

    @Nullable
    @JsonProperty("assigned_to")
    private String assignedTo;
}
