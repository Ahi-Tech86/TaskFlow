package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.project.ProjectCreateRequestDto;
import com.ahicode.TextMe.model.dto.project.ProjectDto;
import com.ahicode.TextMe.model.dto.project.ProjectUpdateRequestDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.model.enums.ProjectRole;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.repository.ProjectRepository;
import com.ahicode.TextMe.service.ProjectService;
import com.ahicode.TextMe.service.factory.DateTimeFactory;
import com.ahicode.TextMe.service.factory.project.ProjectDtoFactory;
import com.ahicode.TextMe.service.factory.project.ProjectEntityFactory;
import com.ahicode.TextMe.service.factory.project.ProjectMemberEntityFactory;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final PermissionChecker permissionChecker;
    private final ProjectDtoFactory dtoFactory;
    private final ProjectRepository repository;
    private final DateTimeFactory dateTimeFactory;
    private final ProjectEntityFactory entityFactory;
    private final ProjectMemberRepository memberRepository;
    private final ProjectMemberEntityFactory memberEntityFactory;

    @Override
    @Transactional
    public ProjectDto createProject(Long userId, String userNickname, ProjectCreateRequestDto requestDto) {
        ProjectEntity project = entityFactory.makeProjectEntity(requestDto, userId);

        ProjectEntity savedProject = repository.saveAndFlush(project);
        log.info("Project saved with ID: {}", savedProject.getId());
        ProjectMemberEntity projectMember = memberEntityFactory.makeProjectMemberEntity(
                savedProject, ProjectRole.PROJECT_MANAGER, userNickname
        );
        memberRepository.saveAndFlush(projectMember);
        log.info("Project member created with ID: {}", projectMember.getId());

        return dtoFactory.makeProjectDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto updateProjectInfo(Long userId, Long projectId, ProjectUpdateRequestDto requestDto) {
        ProjectEntity project = isProjectExistsById(projectId);
        ProjectMemberEntity projectMember = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);
        boolean canUpdateProject = permissionChecker.hasPermission(projectMember, "project", Action.UPDATE, null);

        if (!canUpdateProject) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        if (requestDto.getName() != null) {
            project.setName(requestDto.getName());
        }

        if (requestDto.getDescription() != null) {
            project.setDescription(requestDto.getDescription());
        }

        if (requestDto.getStartDate() != null) {
            project.setStartDate(requestDto.getStartDate());
        }

        ProjectEntity updatedProject = repository.save(project);
        log.info("Project info with id {} was updated", projectId);

        return dtoFactory.makeProjectDto(updatedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByUser(Long userId) {
        List<Tuple> tuples = memberRepository.getAllProjectsByUserId(userId);

        return tuples.stream()
                .map(tuple -> ProjectDto.builder()
                        .name(tuple.get("name", String.class))
                        .description(tuple.get("description", String.class))
                        .createAt(dateTimeFactory.toLocalDateTime(tuple.get("create_at", Instant.class)))
                        .startDate(tuple.get("start_date", Date.class).toLocalDate())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProjectByUser(Long userId, Long projectId) {
        Tuple tuple = memberRepository.getProjectByUserIdAndProjectId(userId, projectId);

        return dtoFactory.makeProjectDto(tuple);
    }

    @Override
    @Transactional
    public void deleteProject(Long userId, Long projectId) {
        isProjectExistsById(projectId);

        ProjectMemberEntity projectMember = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);
        boolean canDeleteProject = permissionChecker.hasPermission(projectMember, "project", Action.DELETE, null);

        if (!canDeleteProject) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        repository.deleteById(projectId);
        log.info("Project with ID {} was delete", projectId);
    }

    private ProjectEntity isProjectExistsById(Long projectId) {
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
