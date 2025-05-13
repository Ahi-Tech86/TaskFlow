package com.ahicode.TextMe.unit.enums;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TaskStatusTest extends EnumFromNameTest<TaskStatus> {

    @Override
    protected TaskStatus fromName(String name) {
        return TaskStatus.fromName(name);
    }

    @Override
    protected TaskStatus getValidEnumValue() {
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    protected String getInvalidEnumName() {
        return "FINISHED";
    }

    @Override
    protected String getExpectedExceptionMessage(String name) {
        return String.format("Task status name: %s does not exist", name);
    }

    @Test
    void testChangeNextStatus_validTransition() {
        assertThat(TaskStatus.changeNextStatus(TaskStatus.TO_DO)).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(TaskStatus.changeNextStatus(TaskStatus.IN_PROGRESS)).isEqualTo(TaskStatus.NEEDS_APPROVAL);
        assertThat(TaskStatus.changeNextStatus(TaskStatus.NEEDS_APPROVAL)).isEqualTo(TaskStatus.DONE);
        assertThat(TaskStatus.changeNextStatus(TaskStatus.DONE)).isEqualTo(TaskStatus.OVERDUE);
    }

    @Test
    void testChangeNextStatus_noNextStatus() {
        assertThatThrownBy(() -> TaskStatus.changeNextStatus(TaskStatus.OVERDUE))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("There is no next status for OVERDUE")
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }
}
