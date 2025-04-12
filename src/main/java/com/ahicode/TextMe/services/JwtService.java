package com.ahicode.TextMe.services;

import com.ahicode.TextMe.enums.AppRole;
import org.springframework.security.core.Authentication;

public interface JwtService {
    boolean isAccessTokenValid(String token);
    boolean isRefreshTokenValid(String token);
    boolean isAccessTokenExpired(String token);
    boolean isRefreshTokenExpired(String token);
    AppRole extractRoleFromAccessToken(String token);
    AppRole extractRoleFromRefreshToken(String token);
    String extractEmailFromAccessToken(String token);
    String extractEmailFromRefreshToken(String token);
    Authentication authenticateAccessToken(String token);
    String generateAccessToken(String email, AppRole role);
    String generateRefreshToken(String email, AppRole role);
}
