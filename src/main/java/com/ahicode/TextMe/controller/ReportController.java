package com.ahicode.TextMe.controller;

import com.ahicode.TextMe.service.CookieService;
import com.ahicode.TextMe.service.JwtService;
import com.ahicode.TextMe.service.producer.ReportProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project/{projectId}/reports")
public class ReportController {

    private final ReportProducer reportProducer;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final String accessTokenCookieName = "accessToken";

    @GetMapping("/get")
    public ResponseEntity<String> getReport(HttpServletRequest request, @PathVariable("projectId") Long projectId) {
        String accessToken = cookieService.extractCookieValueFromCookieByName(request, accessTokenCookieName);
        Long userId = jwtService.extractUserIdFromAccessToken(accessToken);

        reportProducer.processRequest(projectId, userId);
        return ResponseEntity.ok("Report will be send to your email");
    }
}
