package com.ahicode.TextMe.config.jwt;

import com.ahicode.TextMe.config.security.AllowedPathsConfig;
import com.ahicode.TextMe.config.security.jwt.JwtAuthFilter;
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
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;

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
}
