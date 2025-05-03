package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.entity.ProjectMemberEntity;

public interface ProjectMemberValidationService {
    void isUserAlreadyProjectMember(Long projectId, String nickname);
    ProjectMemberEntity isUserProjectMember(Long projectId, String nickname);
}
