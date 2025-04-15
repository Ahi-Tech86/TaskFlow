package com.ahicode.TextMe.controller;

import com.ahicode.TextMe.model.dto.project.ProjectMemberDto;
import com.ahicode.TextMe.service.CookieService;
import com.ahicode.TextMe.service.JwtService;
import com.ahicode.TextMe.service.ProjectMemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project/{projectId}/members")
public class ProjectMemberController {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final ProjectMemberService projectMemberService;
    private final String accessTokenCookieName = "accessToken";

    @PostMapping("/invite")
    public ResponseEntity<ProjectMemberDto> inviteUser(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestParam(name = "role") String inviteeRole,
            @RequestParam(name = "nickname") String inviteeNickname
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long inviterId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectMemberService.inviteInProject(projectId, inviterId, inviteeNickname, inviteeRole));
    }

    @DeleteMapping("/exclude")
    public ResponseEntity<Void> excludeMember(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestParam(name = "nickname") String excludedMemberNickname
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long excluderId = jwtService.extractUserIdFromAccessToken(accessToken);
        projectMemberService.excludeUserFromProject(projectId, excluderId, excludedMemberNickname);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberDto>> getAllProjectMembers(
            HttpServletRequest request,
            @PathVariable Long projectId
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId, userId));
    }

    @PatchMapping("/changeRole")
    public ResponseEntity<ProjectMemberDto> changeRole(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestParam(name = "role") String newRole,
            @RequestParam(name = "nickname") String targetNickname
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long changerId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(projectMemberService.changeRoleForProjectMember(projectId, changerId, targetNickname, newRole));
    }
}
