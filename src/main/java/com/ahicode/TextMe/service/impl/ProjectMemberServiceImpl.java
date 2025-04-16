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

    @Override
    public List<ProjectMemberDto> getProjectMembers(Long projectId, Long userId) {
        isProjectExistsById(projectId);
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
        isProjectExistsById(projectId);
        ProjectMemberEntity inviter = memberRepository.getProjectMemberEntityByProjectIdAndUserId(inviterId, projectId);
        isUserAlreadyProjectMember(inviteeNickname, projectId);
        UserEntity inviteeUser = isUserExistsByNickname(inviteeNickname);

        boolean canInviteUser = permissionChecker.hasPermission(inviter, "members", Action.INVITE_MEMBER, null);
        if (!canInviteUser) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        ProjectRole role = ProjectRole.fromName(inviteeRole);

        ProjectMemberEntity memberEntity = memberEntityFactory
                .makeProjectMemberEntity(inviteeUser.getId(), inviteeNickname, projectId, role);

        ProjectMemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        log.info("Added new user in project with id {}", projectId);

        return memberDtoFactory.makeProjectMemberDto(savedMemberEntity);
    }

    @Override
    public void excludeUserFromProject(Long projectId, Long excluderId, String excludedUserNickname) {
        isProjectExistsById(projectId);
        ProjectMemberEntity excluder = memberRepository.getProjectMemberEntityByProjectIdAndUserId(excluderId, projectId);
        ProjectMemberEntity excludedUser = isUserProjectMember(excludedUserNickname, projectId);

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
        isProjectExistsById(projectId);
        ProjectMemberEntity changer = memberRepository.getProjectMemberEntityByProjectIdAndUserId(changerId, projectId);
        ProjectMemberEntity targetMember = isUserProjectMember(targetNickname, projectId);

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

    private ProjectEntity isProjectExistsById(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(
                () -> {
                    String errorMessage = String.format("Project with id %s doesn't exists", projectId);
                    log.warn("Attempt to change info about non-existent project with id: {}", projectId);
                    return new AppException(errorMessage, HttpStatus.NOT_FOUND);
                }
        );

        return project;
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

    private void isUserAlreadyProjectMember(String nickname, Long projectId) {
        Optional<ProjectMemberEntity> optionalProjectMember = memberRepository
                .getOptionalProjectMemberEntityByProjectIdAndUserNickname(nickname, projectId);

        if (optionalProjectMember.isPresent()) {
            log.error("Attempt to invite user which already is project member with nickname: {}", nickname);
            throw new AppException(
                    String.format("User with nickname %s already is project member", nickname), HttpStatus.BAD_REQUEST
            );
        }
    }

    private ProjectMemberEntity isUserProjectMember(String nickname, Long projectId) {
        Optional<ProjectMemberEntity> optionalProjectMember = memberRepository
                .getOptionalProjectMemberEntityByProjectIdAndUserNickname(nickname, projectId);

        if (optionalProjectMember.isEmpty()) {
            log.error("Attempt to exclude user who is not a member of the project with id: {}", projectId);
            throw new AppException(
                    String.format("The user with the nickname %s is not a member of the project with id %s, so you " +
                            "cannot exclude him", nickname, projectId),
                    HttpStatus.BAD_REQUEST
            );
        } else {
            return optionalProjectMember.get();
        }
    }
}
