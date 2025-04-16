package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.entity.ProjectEntity;

public interface ProjectValidationService {
    ProjectEntity isProjectExistsById(Long projectId);
}
