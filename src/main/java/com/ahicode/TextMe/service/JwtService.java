package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.enums.AppRole;
import org.springframework.security.core.Authentication;

public interface JwtService {
    Long extractUserIdFromAccessToken(String token);
    Long extractUserIdFromRefreshToken(String token);
    boolean isAccessTokenValid(String token);
    boolean isRefreshTokenValid(String token);
    boolean isAccessTokenExpired(String token);
    boolean isRefreshTokenExpired(String token);
    AppRole extractRoleFromAccessToken(String token);
    AppRole extractRoleFromRefreshToken(String token);
    String extractEmailFromAccessToken(String token);
    String extractEmailFromRefreshToken(String token);
    Authentication authenticateAccessToken(String token);
    String generateAccessToken(Long userId, String email, AppRole role);
    String generateRefreshToken(Long userId, String email, AppRole role);
}
