package com.ahicode.TextMe.controller;

import com.ahicode.TextMe.model.dto.task.TaskCreateRequestDto;
import com.ahicode.TextMe.model.dto.task.TaskDto;
import com.ahicode.TextMe.model.dto.task.TaskUpdateRequestDto;
import com.ahicode.TextMe.service.CookieService;
import com.ahicode.TextMe.service.JwtService;
import com.ahicode.TextMe.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    @PatchMapping("/{taskId}/update")
    public ResponseEntity<TaskDto> updateTaskInfo(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId,
            @Valid @RequestBody TaskUpdateRequestDto requestDto
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(taskService.updateTask(projectId, taskId, userId, requestDto));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(taskService.getTask(projectId, taskId, userId));
    }

    @PatchMapping("/{taskId}/changeStatus")
    public ResponseEntity<TaskDto> changeTaskStatus(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(taskService.changeStatus(projectId, taskId, userId));
    }

    @DeleteMapping("/{taskId}/delete")
    public ResponseEntity<Void> deleteTask(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        taskService.deleteTask(projectId, taskId, userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<TaskDto> assignTask(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId,
            @RequestParam("nickname") String nickname
    ) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        return ResponseEntity.ok(taskService.assignTask(projectId, taskId, userId, nickname));
    }
}
