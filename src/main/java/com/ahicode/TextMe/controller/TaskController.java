package com.ahicode.TextMe.controller;

import com.ahicode.TextMe.model.dto.task.TaskCreateRequestDto;
import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.service.CookieService;
import com.ahicode.TextMe.service.JwtService;
import com.ahicode.TextMe.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project/{projectId}/tasks")
public class TaskController {

    private final JwtService jwtService;
    private final TaskService taskService;
    private final CookieService cookieService;
    private final String accessTokenCookieName = "accessToken";

    @PostMapping("/create")
    public ResponseEntity<TaskDto> createTask(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestBody TaskCreateRequestDto requestDto
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, userId, requestDto));
    }
}
