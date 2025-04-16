package com.ahicode.TextMe.model.entity;

import com.ahicode.TextMe.model.enums.TaskPriority;
import com.ahicode.TextMe.model.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    private String title;

    @NotNull
    @Size(min = 3, max = 500)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @NotNull
    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @NotNull
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @NotNull
    @Column(name = "assigned_id")
    private Long assignedId;

    @NotNull
    @Column(name = "creator_id")
    private Long creatorId;
}
