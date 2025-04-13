package com.ahicode.TextMe.config;

import com.ahicode.TextMe.enums.AppRole;
import com.ahicode.TextMe.exceptions.AppException;
import com.ahicode.TextMe.services.CookieService;
import com.ahicode.TextMe.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final int tokenMaxAge = 3600;

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final AllowedPathsConfig allowedPaths;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Getting request path
        String path = request.getRequestURI();

        if (allowedPaths.getAllowedPaths().contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extracting access token from headers or cookies
        String accessToken = extractAccessToken(request);
        // Extracting refresh token from cookies
        String refreshToken = cookieService.extractCookieValueFromCookieByName(request, "refreshToken");

        try {
            handleTokens(request, response, accessToken, refreshToken);
            filterChain.doFilter(request, response);
        } catch (AppException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(exception.getHttpStatus().value(), exception.getMessage());
        }
    }

    private void handleTokens(
            HttpServletRequest request,
            HttpServletResponse response,
            String accessToken,
            String refreshToken
    ) {
        if (isTokenInvalid(refreshToken)) {
            log.error("An attempt was made to gain access with an invalid refresh token or missing refresh token");
            throw new AppException("The refresh token was lost or is not valid", HttpStatus.UNAUTHORIZED);
        }

        if (accessToken == null || jwtService.isAccessTokenExpired(accessToken)) {
            if (jwtService.isRefreshTokenExpired(refreshToken)) {
                log.error("An attempt was made to gain access with an expired refresh token");
                throw new AppException("The refresh token is expired, please authorize again", HttpStatus.UNAUTHORIZED);
            }

            handleRefreshToken(response, refreshToken);

        } else {
            if (!jwtService.isAccessTokenValid(accessToken)) {
                log.error("An attempt was made to gain access with an invalid access token");
                throw new AppException("The access token is not valid", HttpStatus.UNAUTHORIZED);
            }
            authenticateUser(accessToken);
        }
    }

    private boolean isTokenInvalid(String token) {
        return token == null || !jwtService.isRefreshTokenValid(token);
    }

    private void authenticateUser(String token) {
        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(jwtService.authenticateAccessToken(token));
            log.info("Successful authentication occurred");
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
            log.error("An error occurred during user authentication");
            throw new AppException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null) {
            return authHeader.substring(7);
        }

        return cookieService.extractCookieValueFromCookieByName(request, "accessToken");
    }

    private void handleRefreshToken(HttpServletResponse response, String token) {
        AppRole role = jwtService.extractRoleFromRefreshToken(token);
        String email = jwtService.extractEmailFromRefreshToken(token);

        String newAccessToken = jwtService.generateAccessToken(email, role);
        log.info("Generated new access token for a user with email {}", email);

        cookieService.updateCookie(response, newAccessToken, "accessToken", tokenMaxAge);
        authenticateUser(newAccessToken);
    }
}
