package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.dto.project.ProjectCreateRequestDto;
import com.ahicode.TextMe.model.dto.project.ProjectDto;
import com.ahicode.TextMe.model.dto.project.ProjectUpdateRequestDto;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(Long userId, String userNickname, ProjectCreateRequestDto requestDto);
    ProjectDto updateProjectInfo(Long userId, Long projectId, ProjectUpdateRequestDto requestDto);
    List<ProjectDto> getProjectsByUser(Long userId);
    ProjectDto getProjectByUser(Long userId, Long projectId);
    void deleteProject(Long userId, Long projectId);
}
