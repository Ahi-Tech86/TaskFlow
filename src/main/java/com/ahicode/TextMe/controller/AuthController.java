package com.ahicode.TextMe.controller;

import com.ahicode.TextMe.dtos.*;
import com.ahicode.TextMe.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.register(signUpRequest));
    }

    @PostMapping("/activate")
    public ResponseEntity<UserDto> confirmRegister(@RequestBody ActivateRegistrationRequest activateRegistrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.activateRegistration(activateRegistrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> authenticateUser(HttpServletResponse response, @RequestBody SignInRequest signInRequest) {
        AuthResponse authenticatedUser = authService.login(signInRequest);

        UserDto userDto = authenticatedUser.getUserDto();
        String accessToken = authenticatedUser.getAccessToken();
        String refreshToken = authenticatedUser.getRefreshToken();

        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(ACCESS_TOKEN_COOKIE_NAME, response);
        deleteCookie(REFRESH_TOKEN_COOKIE_NAME, response);

        return ResponseEntity.noContent().build();
    }

    private void deleteCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
