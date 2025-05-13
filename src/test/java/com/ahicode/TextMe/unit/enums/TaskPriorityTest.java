package com.ahicode.TextMe.unit.enums;

import com.ahicode.TextMe.model.enums.TaskPriority;

public class TaskPriorityTest extends EnumFromNameTest<TaskPriority> {

    @Override
    protected TaskPriority fromName(String name) {
        return TaskPriority.fromName(name);
    }

    @Override
    protected TaskPriority getValidEnumValue() {
        return TaskPriority.MEDIUM;
    }

    @Override
    protected String getInvalidEnumName() {
        return "INVALID_TASK_PRIORITY_NAME";
    }

    @Override
    protected String getExpectedExceptionMessage(String name) {
        return String.format("Task priority name: %s does not exist", name);
    }
}
