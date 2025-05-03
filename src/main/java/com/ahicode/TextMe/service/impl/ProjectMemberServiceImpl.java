package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.project.ProjectMemberDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.entity.UserEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.model.enums.ProjectRole;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.repository.ProjectRepository;
import com.ahicode.TextMe.repository.UserRepository;
import com.ahicode.TextMe.service.ProjectMemberService;
import com.ahicode.TextMe.service.ProjectMemberValidationService;
import com.ahicode.TextMe.service.ProjectValidationService;
import com.ahicode.TextMe.service.factory.project.ProjectMemberDtoFactory;
import com.ahicode.TextMe.service.factory.project.ProjectMemberEntityFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final UserRepository userRepository;
    private final PermissionChecker permissionChecker;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectMemberDtoFactory memberDtoFactory;
    private final ProjectMemberEntityFactory memberEntityFactory;
    private final ProjectValidationService projectValidationService;
    private final ProjectMemberValidationService projectMemberValidationService;

    @Override
    public List<ProjectMemberDto> getProjectMembers(Long projectId, Long userId) {
        projectValidationService.isProjectExistsById(projectId);
        ProjectMemberEntity user = memberRepository
                .getOptionalProjectMemberEntityByProjectIdAndUserId(userId, projectId)
                .orElseThrow(
                        () -> new AppException(
                                String.format("User is not member of project with id %s", projectId),
                                HttpStatus.NOT_FOUND
                        )
                );

        boolean canViewProjectMembersList = permissionChecker
                .hasPermission(user, "members", Action.VIEW_LIST_OF_MEMBERS, null);

        if (!canViewProjectMembersList) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        List<ProjectMemberEntity> projectMembers = memberRepository.getAllProjectMembers(projectId);
        List<ProjectMemberDto> responseBody = new ArrayList<>();

        for (ProjectMemberEntity entity : projectMembers) {
            responseBody.add(memberDtoFactory.makeProjectMemberDto(entity));
        }

        return responseBody;
    }

    @Override
    public ProjectMemberDto inviteInProject(Long projectId, Long inviterId, String inviteeNickname, String inviteeRole) {
        ProjectEntity project = projectValidationService.isProjectExistsById(projectId);
        ProjectMemberEntity inviter = memberRepository.getProjectMemberEntityByProjectIdAndUserId(inviterId, projectId);
        UserEntity inviteeUser = isUserExistsByNickname(inviteeNickname);
        projectMemberValidationService.isUserAlreadyProjectMember(projectId, inviteeNickname);

        boolean canInviteUser = permissionChecker.hasPermission(inviter, "members", Action.INVITE_MEMBER, null);
        if (!canInviteUser) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        ProjectRole role = ProjectRole.fromName(inviteeRole);

        ProjectMemberEntity memberEntity = memberEntityFactory
                .makeProjectMemberEntity(inviteeUser, project, role);

        ProjectMemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        log.info("Added new user in project with id {}", projectId);

        return memberDtoFactory.makeProjectMemberDto(savedMemberEntity);
    }

    @Override
    public void excludeUserFromProject(Long projectId, Long excluderId, String excludedUserNickname) {
        projectValidationService.isProjectExistsById(projectId);
        ProjectMemberEntity excluder = memberRepository.getProjectMemberEntityByProjectIdAndUserId(excluderId, projectId);
        ProjectMemberEntity excludedUser = projectMemberValidationService.isUserProjectMember(projectId, excludedUserNickname);

        boolean canExcludeUser = permissionChecker.hasPermission(excluder, "members", Action.EXCLUDE_MEMBER, excludedUser);
        if (!canExcludeUser) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        memberRepository.deleteById(excludedUser.getId());
        log.info("User with nickname {} has been excluded from project with id {}", excludedUserNickname, projectId);
    }

    @Override
    public ProjectMemberDto changeRoleForProjectMember(Long projectId, Long changerId, String targetNickname, String newRole) {
        projectValidationService.isProjectExistsById(projectId);
        ProjectMemberEntity changer = memberRepository.getProjectMemberEntityByProjectIdAndUserId(changerId, projectId);
        ProjectMemberEntity targetMember = projectMemberValidationService.isUserProjectMember(projectId, targetNickname);

        boolean canChangeRole = permissionChecker.hasPermission(changer, "members", Action.CHANGE_MEMBER_ROLE, null);
        if (!canChangeRole) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        ProjectRole role = ProjectRole.fromName(newRole);
        targetMember.setRole(role);

        ProjectMemberEntity savedMember = memberRepository.save(targetMember);
        log.info("Member of project with id {} with nickname got a new role", projectId, targetNickname);

        return memberDtoFactory.makeProjectMemberDto(savedMember);
    }

    private UserEntity isUserExistsByNickname(String nickname) {
        UserEntity user = userRepository.findByNickname(nickname).orElseThrow(
                () -> {
                    String errorMessage = String.format("User with nickname %s doesn't exists", nickname);
                    log.warn("Attempt to invite non-existent user with nickname: {}", nickname);
                    return new AppException(errorMessage, HttpStatus.NOT_FOUND);
                }
        );

        return user;
    }
}
