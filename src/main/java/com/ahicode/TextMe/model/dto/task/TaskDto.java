package com.ahicode.TextMe.model.dto.task;

import com.ahicode.TextMe.model.enums.TaskPriority;
import com.ahicode.TextMe.model.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    @JsonProperty("due_date")
    private LocalDate dueDate;
    @JsonProperty("create_at")
    private LocalDateTime createAt;
    @JsonProperty("update_at")
    private LocalDateTime updateAt;
    @JsonProperty("assigned_to")
    private String assignedNickname;
    @JsonProperty("creator")
    private String creator;
}
