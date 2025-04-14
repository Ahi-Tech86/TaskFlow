package com.ahicode.TextMe.controller;

import com.ahicode.TextMe.model.dto.project.ProjectCreateRequestDto;
import com.ahicode.TextMe.model.dto.project.ProjectDto;
import com.ahicode.TextMe.model.dto.project.ProjectUpdateRequestDto;
import com.ahicode.TextMe.service.CookieService;
import com.ahicode.TextMe.service.JwtService;
import com.ahicode.TextMe.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@RequestMapping("/api/project")
public class ProjectController {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final ProjectService projectService;

    private final String accessTokenCookieName = "accessToken";

    @PostMapping("/create")
    public ResponseEntity<ProjectDto> createProject(HttpServletRequest request, @Valid @RequestBody ProjectCreateRequestDto requestDto) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);

        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);
        String userNickname = jwtService.extractEmailFromAccessToken(accessToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(userId, userNickname, requestDto));
    }

    @PatchMapping("/{projectId}/update")
    public ResponseEntity<ProjectDto> updateProject(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequestDto requestDto
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);

        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(projectService.updateProjectInfo(userId, projectId, requestDto));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProject(HttpServletRequest request, @PathVariable Long projectId) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(projectService.getProjectByUser(userId, projectId));
    }

    @GetMapping("/user/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjectsByUser(HttpServletRequest request) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(projectService.getProjectsByUser(userId));
    }

    @DeleteMapping("/{projectId}/delete")
    public ResponseEntity<Void> deleteProject(HttpServletRequest request, @PathVariable Long projectId) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        projectService.deleteProject(userId, projectId);

        return ResponseEntity.noContent().build();
    }
}
