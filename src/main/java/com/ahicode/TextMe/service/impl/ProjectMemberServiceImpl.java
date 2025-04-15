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
        return List.of();
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

        ProjectRole role = getProjectRole(inviteeRole);

        ProjectMemberEntity memberEntity = memberEntityFactory
                .makeProjectMemberEntity(inviteeUser.getId(), inviteeNickname, projectId, role);

        ProjectMemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        log.info("Added new user in project with id {}", projectId);

        return memberDtoFactory.makeProjectMemberDto(savedMemberEntity);
    }

    @Override
    public void excludeUserFromProject(Long projectId, Long excluderId, String excludedUserNickname) {

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

    private ProjectRole getProjectRole(String inviteeRole) {
        try {
            return ProjectRole.valueOf(inviteeRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Attempt to assign a non-existent role {} a user", inviteeRole);
            throw new AppException(String.format("Role with the name %s do not exist", inviteeRole), HttpStatus.BAD_REQUEST);
        }
    }
}
