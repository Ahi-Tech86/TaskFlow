package com.ahicode.TextMe.unit.config.jwt;

import com.ahicode.TextMe.config.security.AllowedPathsConfig;
import com.ahicode.TextMe.config.security.jwt.JwtAuthFilter;
import com.ahicode.TextMe.model.enums.AppRole;
import com.ahicode.TextMe.service.CookieService;
import com.ahicode.TextMe.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthFilterTest {

    private JwtService jwtService;
    private JwtAuthFilter jwtAuthFilter;
    private CookieService cookieService;
    private AllowedPathsConfig allowedPaths;

    @BeforeEach
    void setup() {
        jwtService = mock(JwtService.class);
        cookieService = mock(CookieService.class);
        allowedPaths = mock(AllowedPathsConfig.class);
        jwtAuthFilter = new JwtAuthFilter(jwtService, cookieService, allowedPaths);

        when(allowedPaths.getAllowedPaths())
                .thenReturn(List.of("/api/auth/login", "/api/auth/register", "/api/auth/activate"));
    }

    @Test
    void shouldAllowAccessToAllowedPaths() throws ServletException, IOException {
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/api/auth/login");

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldSendAuthenticationErrorWhenTokensAreExpired() throws ServletException, IOException {
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/api/protected");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(jwtService.isAccessTokenExpired(any())).thenReturn(true);
        when(jwtService.isRefreshTokenExpired(any())).thenReturn(true);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(response, times(1))
                .sendError(HttpStatus.UNAUTHORIZED.value(), "The refresh token was lost or is not valid");
    }

    @Test
    void shouldAuthenticateUserWithValidAccessToken() throws ServletException, IOException {
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String validAccessToken = "validAccessToken";
        String validRefreshToken = "validRefreshToken";
        Cookie refreshTokenCookie = new Cookie("refreshToken", validRefreshToken);

        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + validAccessToken);
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(cookieService.extractCookieValueFromCookieByName(request, "refreshToken")).thenReturn(validRefreshToken);
        when(jwtService.isAccessTokenValid(validAccessToken)).thenReturn(true);
        when(jwtService.isAccessTokenExpired(validAccessToken)).thenReturn(false);
        when(jwtService.isRefreshTokenValid(validRefreshToken)).thenReturn(true);
        when(jwtService.isRefreshTokenExpired(validRefreshToken)).thenReturn(false);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(jwtService, times(1)).authenticateAccessToken(validAccessToken);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldGenerateNewAccessTokenWithValidRefreshToken() throws ServletException, IOException {
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String newAccessToken = "newAccessToken";
        String validRefreshToken = "validRefreshToken";
        Cookie refreshTokenCookie = new Cookie("refreshToken", validRefreshToken);

        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtService.isAccessTokenExpired(null)).thenReturn(true);
        when(jwtService.isRefreshTokenValid(validRefreshToken)).thenReturn(true);
        when(jwtService.isRefreshTokenExpired(validRefreshToken)).thenReturn(false);
        when(jwtService.extractUserIdFromRefreshToken(validRefreshToken)).thenReturn(1L);
        when(jwtService.extractRoleFromRefreshToken(validRefreshToken)).thenReturn(AppRole.USER);
        when(jwtService.extractEmailFromRefreshToken(validRefreshToken)).thenReturn("mock@mail.com");
        when(jwtService.generateAccessToken(1L, "mock@mail.com", AppRole.USER)).thenReturn(newAccessToken);
        when(cookieService.extractCookieValueFromCookieByName(request, "refreshToken")).thenReturn(validRefreshToken);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(jwtService, times(1)).authenticateAccessToken(newAccessToken);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldThrowErrorForInvalidAccessToken() throws ServletException, IOException {
        String invalidAccessToken = "invalidAccessToken";
        String validRefreshToken = "validRefresherToken";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie refreshTokenCookie = new Cookie("refreshToken", validRefreshToken);

        when(request.getRequestURI()).thenReturn("/auth/protected");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + invalidAccessToken);
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtService.isRefreshTokenExpired(validRefreshToken)).thenReturn(false);
        when(jwtService.isRefreshTokenValid(validRefreshToken)).thenReturn(true);
        when(jwtService.isAccessTokenExpired(invalidAccessToken)).thenReturn(false);
        when(jwtService.isAccessTokenValid(invalidAccessToken)).thenReturn(false);
        when(cookieService.extractCookieValueFromCookieByName(request, "refreshToken")).thenReturn(validRefreshToken);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(response, times(1))
                .sendError(HttpStatus.UNAUTHORIZED.value(), "The access token is not valid");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldThrowErrorForMissingRefreshToken() throws ServletException, IOException {
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/api/protected");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(WebUtils.getCookie(request, "refreshToken")).thenReturn(null);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(response, times(1))
                .sendError(HttpStatus.UNAUTHORIZED.value(), "The refresh token was lost or is not valid");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldHandleInvalidRefreshToken() throws ServletException, IOException {
        String refreshToken = "invalidRefreshToken";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtService.isAccessTokenExpired(any())).thenReturn(true);
        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(false);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(response, times(1))
                .sendError(HttpStatus.UNAUTHORIZED.value(), "The refresh token was lost or is not valid");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldThrowErrorForExpiredRefreshToken() throws ServletException, IOException {
        String refreshToken = "expiredRefreshToken";
        FilterChain filterChain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        when(request.getRequestURI()).thenReturn("/auth/v1/protected");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtService.isAccessTokenExpired(null)).thenReturn(true);
        when(jwtService.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtService.isRefreshTokenExpired(refreshToken)).thenReturn(true);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(response, times(1))
                .sendError(HttpStatus.UNAUTHORIZED.value(), "The refresh token was lost or is not valid");
        verify(filterChain, never()).doFilter(request, response);
    }
}
