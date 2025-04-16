package com.ahicode.TextMe.model.entity;

import com.ahicode.TextMe.model.enums.ProjectRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_member")
public class ProjectMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "nickname")
    private String memberNickname;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    @NotNull
    @Column(name = "joined_at")
    private LocalDate joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
}
