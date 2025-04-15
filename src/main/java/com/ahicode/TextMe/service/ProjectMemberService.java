package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.dto.project.ProjectMemberDto;

import java.util.List;

public interface ProjectMemberService {
    List<ProjectMemberDto> getProjectMembers(Long projectId, Long userId);
    void excludeUserFromProject(Long projectId, Long excluderId, String excludedUserNickname);
    ProjectMemberDto inviteInProject(Long projectId, Long inviterId, String inviteeNickname, String inviteeRole);
    ProjectMemberDto changeRoleForProjectMember(Long projectId, Long changerId, String targetNickname, String newRole);
}
