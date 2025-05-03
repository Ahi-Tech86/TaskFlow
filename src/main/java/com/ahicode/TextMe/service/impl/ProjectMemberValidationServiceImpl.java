package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.service.ProjectMemberValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMemberValidationServiceImpl implements ProjectMemberValidationService {

    private final ProjectMemberRepository repository;

    @Override
    public void isUserAlreadyProjectMember(Long projectId, String nickname) {
        Optional<ProjectMemberEntity> optionalMember = repository.findOptionalByNicknameAndProjectId(nickname, projectId);

        if (optionalMember.isPresent()) {
            log.error("Attempt to invite user which already is project member with nickname: {}", nickname);
            throw new AppException(
                    String.format("User with nickname %s already is project member", nickname), HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public ProjectMemberEntity isUserProjectMember(Long projectId, String nickname) {
        Optional<ProjectMemberEntity> optionalMember = repository.findOptionalByNicknameAndProjectId(nickname, projectId);

        if (optionalMember.isEmpty()) {
            log.error("Attempt to exclude user who is not a member of the project with id: {}", projectId);
            throw new AppException(
                    String.format("The user with the nickname %s is not a member of the project with id %s, so you " +
                            "cannot exclude him", nickname, projectId),
                    HttpStatus.BAD_REQUEST
            );
        } else {
            return optionalMember.get();
        }
    }
}
