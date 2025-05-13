package com.ahicode.TextMe.unit.enums;

import com.ahicode.TextMe.model.enums.ProjectRole;

public class ProjectRoleTest extends EnumFromNameTest<ProjectRole> {

    @Override
    protected ProjectRole fromName(String name) {
        return ProjectRole.fromName(name);
    }

    @Override
    protected ProjectRole getValidEnumValue() {
        return ProjectRole.PROJECT_MANAGER;
    }

    @Override
    protected String getInvalidEnumName() {
        return "INVALID_ROLE";
    }

    @Override
    protected String getExpectedExceptionMessage(String name) {
        return String.format("Role %s does not exist", name);
    }
}
