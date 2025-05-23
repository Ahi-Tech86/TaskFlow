package com.ahicode.TextMe.model.dto.report;

import com.ahicode.TextMe.model.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProjectStats {
    private ProjectRole role;
    private LocalDateTime joinedAt;
    private Long currentTasks;
    private Long completedTasks;
    private BigDecimal completedTasksPercentage;
}
