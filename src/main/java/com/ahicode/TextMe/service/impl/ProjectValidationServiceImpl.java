package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.repository.ProjectRepository;
import com.ahicode.TextMe.service.ProjectValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectValidationServiceImpl implements ProjectValidationService {

    private final ProjectRepository repository;

    @Override
    public ProjectEntity isProjectExistsById(Long projectId) {
        ProjectEntity project = repository.findById(projectId).orElseThrow(
                () -> {
                    String errorMessage = String.format("Project with id %s doesn't exists", projectId);
                    log.warn("Attempt to change info about non-existent project with id: {}", projectId);
                    return new AppException(errorMessage, HttpStatus.NOT_FOUND);
                }
        );

        return project;
    }
}
