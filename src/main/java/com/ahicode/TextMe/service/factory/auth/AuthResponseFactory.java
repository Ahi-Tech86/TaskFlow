package com.ahicode.TextMe.service.factory.auth;

import com.ahicode.TextMe.model.dto.auth.AuthResponse;
import com.ahicode.TextMe.model.dto.user.UserDto;
import org.springframework.stereotype.Component;

@Component
public class AuthResponseFactory {
    public AuthResponse makeAuthResponse(UserDto userDto, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userDto(userDto)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
